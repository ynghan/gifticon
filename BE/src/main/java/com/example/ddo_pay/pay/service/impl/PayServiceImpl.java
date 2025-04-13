package com.example.ddo_pay.pay.service.Impl;

import com.example.ddo_pay.client.BankClient;
import com.example.ddo_pay.common.exception.CustomException;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.common.util.RedisHandler;
import com.example.ddo_pay.gift.entity.Gift;
import com.example.ddo_pay.gift.repository.GiftRepository;
import com.example.ddo_pay.pay.dto.bank_request.BankDdoPayChargeRequest;
import com.example.ddo_pay.pay.dto.bank_request.PosRequest;
import com.example.ddo_pay.pay.dto.bank_request.TokenEqualResponseDto;
import com.example.ddo_pay.pay.dto.bank_response.BankChargeResponseDto;
import com.example.ddo_pay.pay.dto.finance.DepositAccountWithdrawRequest;
import com.example.ddo_pay.pay.dto.request.*;
import com.example.ddo_pay.pay.dto.response.GetAccountResponse;
import com.example.ddo_pay.pay.dto.response.GetBalanceResponse;
import com.example.ddo_pay.pay.dto.response.GetHistoryListResponse;
import com.example.ddo_pay.pay.dto.response.GetPointResponse;
import com.example.ddo_pay.pay.entity.Account;
import com.example.ddo_pay.pay.entity.AssetType;
import com.example.ddo_pay.pay.entity.DdoPay;
import com.example.ddo_pay.pay.entity.History;
import com.example.ddo_pay.pay.finance_api.FinanceClient;
import com.example.ddo_pay.pay.repository.AccountRepository;
import com.example.ddo_pay.pay.repository.DdoPayRepository;
import com.example.ddo_pay.pay.repository.HistoryRepository;
import com.example.ddo_pay.pay.service.PayService;
import com.example.ddo_pay.sse.SseService;
import com.example.ddo_pay.user.entity.User;
import com.example.ddo_pay.user.service.impl.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.ddo_pay.pay.entity.AssetType.BALANCE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayServiceImpl implements PayService {

    private final RestTemplate restTemplate;
    private final FinanceClient financeClient;
    private final RedisHandler redisHandler;
    private final ObjectMapper objectMapper;
    private final DdoPayRepository ddoPayRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final GiftRepository giftRepository;
    private final BankClient bankClient;
    private final HistoryRepository historyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SseService sseService;


    // 유효 계좌 인증
    @Override
    public String verifyAccount(Long userId, AccountVerifyRequest request) {
        String accountNumber = request.getAccountNo();
        String randomMemo = generateRandomMemo(); // 랜덤 단어 생성

        DepositAccountWithdrawRequest dto = DepositAccountWithdrawRequest.of(accountNumber, randomMemo);

        ResponseEntity<?> response = financeClient.sendOneWonTransfer(dto);

        if (response == null) {
            return "ERR_API";
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("금융망 응답 실패: " + response.getBody());
            return "ERR_API";
        }

        try {

            JsonNode root = objectMapper.readTree((String) response.getBody());
            String responseCode = root.path("Header").path("responseCode").asText();

            System.out.println("금융망 응답 코드: " + responseCode);

            if ("H0000".equals(responseCode)) {
                String key = "userId:" + userId;
                String value = "word:" + randomMemo + ",accountNo:" + accountNumber;

                redisHandler.executeOperation(() ->
                        redisHandler.getValueOperations().set(key, value, Duration.ofMinutes(6))
                );
                System.out.println("Redis 저장 완료 → key: " + key + " / value: " + value);

            } else if ("A1003".equals(responseCode)) {
                redisHandler.deleteKey("userId:" + userId);
                System.out.println("Redis 키 삭제: userId:" + userId + " (유효하지 않은 계좌)");
            }

            return responseCode;

        } catch (IOException e) {
            return "ERR_PARSING";
        }
    }


    // 랜덤 영단어 api 호출
    private String generateRandomMemo() {
        String[] response = restTemplate.getForObject("https://random-word-api.herokuapp.com/word", String[].class);
        return (response != null && response.length > 0) ? response[0] : "default";
    }

    // 랜덤 단어 확인 후 계좌 등록
    @Override
    public void registerAccount(Long userId, RegisterAccountRequest request) {
        String inputWord  = request.getRandomWord();
        String redisKey = "userId:" + userId;

        String redisValue = (String) redisHandler.getValueOperations().get(redisKey);
        if (redisValue == null) {
            throw new CustomException(ResponseCode.REDIS_NOT_FOUND);
        }

        System.out.println("입력받은 단어 = " + inputWord);

        String[] parts = redisValue.split(",");
        String storedWord = null;
        String storedAccountNo = null;
        for (String part : parts) {
            if (part.startsWith("word:")) {
                storedWord = part.substring("word:".length());
            } else if (part.startsWith("accountNo:")) {
                storedAccountNo = part.substring("accountNo:".length());
            }
        }

        if (storedWord == null || storedAccountNo == null) {
            throw new CustomException(ResponseCode.INVALID_REDIS_FORMAT);
        }

        // 단어 비교
        if (!storedWord.equalsIgnoreCase(inputWord.trim())) {
            throw new CustomException(ResponseCode.NOT_VERIFIED_ACCOUNT);
        }

        // 사용자 & 또페이 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));

        DdoPay ddoPay = ddoPayRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_DDOPAY));

        // 계좌 저장
        Account account = Account.builder()
                .accountNum(storedAccountNo)
                .ddoPay(ddoPay)
                .build();

        accountRepository.save(account);

        // Redis 삭제
        redisHandler.deleteKey(redisKey);

    }


    // 비밀번호 등록 및 또페이 생성
    @Override
    public void registerPayPassword(Long userId, RegisterPasswordRequest request) {
        String password = request.getPassword();


        // 비밀번호 6자리 숫자인지 검증
        if (!password.matches("^\\d{6}$")) {
            throw new CustomException(ResponseCode.INVALID_PAY_PASSWORD);
        }

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));

        // 이미 또페이가 등록되어 있다면 예외 처리
        if (ddoPayRepository.existsByUser(user)) {
            throw new CustomException(ResponseCode.ALREADY_REGISTERED_DDOPAY);
        }

        // 또페이 생성
        DdoPay ddoPay = DdoPay.builder()
                .user(user)
                .balance(0)
                .point(0)
                .payPassword(password)
                .build();

        // 양방향 연관 관계 설정
        user.changeDdoPay(ddoPay);

        ddoPayRepository.save(ddoPay);
    }

    // 잔고 조회
    @Override
    public GetBalanceResponse selectBalance(Long userId) {
        log.debug("🔍 [selectBalance] 잔고 조회 요청 - userId: {}", userId);
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));
        int balance = findUser.getDdoPay().getBalance();
        return new GetBalanceResponse(balance);
    }

    // 포인트 조회
    @Override
    public GetPointResponse selectPoint(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));
        int point = findUser.getDdoPay().getPoint();
        return new GetPointResponse(point);
    }

    // 계좌 조회
    @Override
    public List<GetAccountResponse> selectAccountList(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));

        // 사용자의 DdoPay 엔티티에서 계좌 목록 가져오기
        List<Account> accountList = findUser.getDdoPay().getAccountList();

        if (accountList.isEmpty()) {
            throw new CustomException(ResponseCode.NO_EXIST_ACCOUNT);
        }

        // 각 Account를 GetAccountResponse로 변환하여 리스트 반환
        return accountList.stream()
                .map(GetAccountResponse::from)
                .collect(Collectors.toList());
    }


    // 기프티콘 생성 시 또페이 잔액 조회 후 출금(잔액 변경)
    @Transactional
    @Override
    public void withdrawDdoPay(Long userId, int amount) {
        DdoPay ddoPay = ddoPayRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_DDOPAY));

        if (!ddoPay.isAvailableToPay(amount)) {
            throw new CustomException(ResponseCode.INSUFFICIENT_BALANCE);
        }

        ddoPay.decreaseBalance(amount);
        // 결제 내역 추가
        History history = History.builder()
                .title("기프티콘 생성")
                .time(LocalDateTime.now())
                .inOutAmount(amount*-1)
                .type(BALANCE)
                .ddoPay(ddoPay)
                .build();
        ddoPay.getHistoryList().add(history);
        ddoPayRepository.save(ddoPay);
        historyRepository.save(history);
    }

    // 기프티콘 취소 환불 시 90% 금액 환불
    @Transactional
    @Override
    public void depositDdoPay(Long userId, int amount) {
        DdoPay ddoPay = ddoPayRepository.findByUserId(userId).orElseThrow(()
                -> new CustomException(ResponseCode.NO_EXIST_DDOPAY));

        ddoPay.increaseBalance(amount);

        // 결제 내역 추가
        History history = History.builder()
                .title("기프티콘 환불")
                .time(LocalDateTime.now())
                .inOutAmount(amount)
                .type(BALANCE)
                .ddoPay(ddoPay)
                .build();
        ddoPay.getHistoryList().add(history);
        ddoPayRepository.save(ddoPay);
        historyRepository.save(history);
    }

    // 또페이 충전
    @Transactional
    @Override
    public void transferDdoPay(Long userId, ChargeDdoPayRequest request) {
        DdoPay ddoPay = ddoPayRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_DDOPAY));

        // 비밀번호가 일치하지 않다면,
        if (!ddoPay.checkPassword(request.getPassword())){
            throw new CustomException(ResponseCode.DIFFRENT_PASSWORD);
        }

        // 사용자 계좌 가져오기. 하나만 있다고 가정
        if(ddoPay.getAccountList().isEmpty()) {
            throw new CustomException(ResponseCode.NO_EXIST_ACCOUNT);
        }
        String accountNum = ddoPay.getAccountList().get(0).getAccountNum();

        BankDdoPayChargeRequest bankRequest = BankDdoPayChargeRequest.builder()
                .userAccountNum(accountNum)
                .corporationAccountNum("9990627419918613")
                .amount(request.getAmount())
                .build();

        log.info("Feign 요청 바디: userAccountNum={}, corpAccountNum={}, amount={}",
                bankRequest.getUserAccountNum(),
                bankRequest.getCorporationAccountNum(),
                bankRequest.getAmount()
        );


        ResponseEntity<BankChargeResponseDto> bankResponse = bankClient.chargeDdoPay(bankRequest);

        BankChargeResponseDto responseBody = bankResponse.getBody();
        log.info("Feign 전체 응답 = {}", bankResponse);
        log.info("Feign 응답 바디 = {}", bankResponse.getBody());

        if (responseBody == null || responseBody.getHeader() == null) {
            throw new CustomException(ResponseCode.INTERNAL_SERVER_ERROR, "bank", "응답이 비어 있음");
        }

        String responseCode = responseBody.getHeader().getResponseCode();


        // 성공 코드가 오면 balance 변경, history 생성
        if ("H0000".equals(responseCode)) {
            ddoPay.plueBalance(request.getAmount());
            ddoPayRepository.save(ddoPay);

            // History 생성 및 연결
            History history = new History();
            history.setTitle("또페이 충전");
            history.setTime(LocalDateTime.now());
            history.setInOutAmount(request.getAmount());
            history.setType(BALANCE);
            history.setDdoPay(ddoPay);

            ddoPay.getHistoryList().add(history);

        } else if ("A1014".equals(responseCode)) {
            throw new CustomException(ResponseCode.INSUFFICIENT_BALANCE, "잔액 부족", "출금 계좌 잔액 부족");
        } else throw new CustomException(ResponseCode.INTERNAL_SERVER_ERROR, "bank", "은행 응답 실패 코드: " + responseCode);



    }

    // 같다면, 다음 로직 실행(계좌 이체 요청(feignclient) -> 깊티 상태 변경(Service) -> 성공 응답(SSE))
    // 다르면, 프론트로 실패 응답(SSE) + pos로 실패 응답(REST)
    @Transactional
    @Override
    public void posPayment(TokenEqualResponseDto request) throws JsonProcessingException {
        log.info("TokenEqualResponseDto: {}, 금액: {}, 가맹점 계좌: {}",
                request.getPaymentToken(),
                request.getPaymentAmount(),
                request.getStoreAccount());

        if (!request.getResult()) {
            if (request.getUserId() != null) {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("status", "FAIL");
                eventData.put("message", "유효하지 않은 결제 토큰입니다.");
                String jsonData = new ObjectMapper().writeValueAsString(eventData);
                sseService.sendToUser(request.getUserId(), "token-validation-fail", jsonData);
            }
            throw new CustomException(ResponseCode.INVALID_PAYMENT_TOKEN);
        }

        log.info("일치된 로직 실행");
        final double FEE_RATE = 0.01;
        final double REWARD_RATE = 0.005;

        int fee = (int) (request.getPaymentAmount() * FEE_RATE);
        int paymentAmount = request.getPaymentAmount() - fee;

        BankDdoPayChargeRequest bankRequest = BankDdoPayChargeRequest.builder()
                .userAccountNum("9990627419918613")
                .corporationAccountNum(request.getStoreAccount())
                .amount(paymentAmount)
                .build();

        log.info("계좌 이체할 금액 : {}", paymentAmount);
        ResponseEntity<BankChargeResponseDto> response = bankClient.chargeDdoPay(bankRequest);

        if (!response.getStatusCode().is2xxSuccessful()) {
            handleBankFailure(request.getUserId());
            throw new CustomException(ResponseCode.BANK_TRANSACTION_FAILED);
        }

        BankChargeResponseDto responseBody = response.getBody();
        if (responseBody == null || responseBody.getHeader() == null) {
            throw new CustomException(ResponseCode.INTERNAL_SERVER_ERROR, "bank", "은행 응답 바디 없음");
        }

        Gift gift = giftRepository.findById(request.getGiftId())
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_GIFTICON));
        DdoPay ddoPay = ddoPayRepository.findByUser(gift.getUser())
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_DDOPAY));
        ddoPay.addPoint((int) (request.getPaymentAmount() * REWARD_RATE));
        ddoPayRepository.save(ddoPay);

        String key = "token:" + request.getPaymentToken();
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        if (redisValue == null) {
            throw new CustomException(ResponseCode.REDIS_NOT_FOUND, "결제 토큰 없음", "key: " + key);
        }

        Long giftId = null;
        Long userId = null;
        for (String part : redisValue.split(",")) {
            String[] split = part.split(":");
            if (split.length != 2) continue;
            if (part.startsWith("giftId:")) giftId = Long.valueOf(split[1]);
            else if (part.startsWith("userId:")) userId = Long.valueOf(split[1]);
        }

        log.info("userId = {}, giftId={}", userId, giftId);
        gift.changeUsedAfter();
        giftRepository.save(gift);
        log.info("기프티콘 상태 변경 및 저장 완료");

        if (userId != null) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("status", "SUCCESS");
            eventData.put("message", "결제가 성공적으로 처리되었습니다.");
            eventData.put("giftId", giftId);
            eventData.put("amount", paymentAmount);
            String jsonData = new ObjectMapper().writeValueAsString(eventData);
            sseService.sendToUser(userId, "payment-success", jsonData);
            log.info("SSE 응답 전송 완료");
        }

        redisTemplate.delete(key);
    }

    private void handleBankFailure(Long userId) throws JsonProcessingException {
        if (userId != null) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("status", "FAIL");
            eventData.put("message", "결제 처리 중 은행 오류가 발생했습니다.");
            String jsonData = new ObjectMapper().writeValueAsString(eventData);
            sseService.sendToUser(userId, "payment-fail", jsonData);
            log.info("SSE 실패 응답 전송");
        }
    }


    // 기프티콘 결제 시 비밀번호 조회
    @Override
    public Boolean verifyGiftPassword(Long userId, String inputPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));

        DdoPay ddoPay = user.getDdoPay();
        if (ddoPay == null) {
            throw new CustomException(ResponseCode.NO_EXIST_DDOPAY);
        }
        return ddoPay.checkPassword(inputPassword);
    }

    // 결제 내역 조회
    @Override
    public List<GetHistoryListResponse> selectHistoryList(Long userId, SelectHistoryRequest request) {
        AssetType assetType;
        try {
            assetType = AssetType.valueOf(request.getHistoryType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 결제 내역 타입: " + request.getHistoryType());
        }
        User findUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));
        Long payId = findUser.getDdoPay().getId();
        List<History> histories = historyRepository.findByDdoPay_IdAndType(payId, assetType);
        List<GetHistoryListResponse> responseList = histories.stream().map(history -> {
            GetHistoryListResponse response = new GetHistoryListResponse();
            response.setId(history.getId());
            response.setTitle(history.getTitle());
            response.setTime(history.getTime());
            response.setInOutAmount(history.getInOutAmount());
            response.setType(history.getType());
            return response;
        }).collect(Collectors.toList());

        return responseList;
    }

    // 같은지 다른지만 확인하고 dto에 결과값 반영해서 응답하기
    @Override
    public TokenEqualResponseDto comparePaymentToken(PosRequest request) {
        String paymentToken = request.getPaymentToken();
        String key = "token:" + paymentToken;
        String redisValue = (String) redisTemplate.opsForValue().get(key);

        // ✅ null 체크 추가
        if (redisValue == null) {
            throw new CustomException(ResponseCode.REDIS_NOT_FOUND, "결제 토큰 데이터 없음", "key: " + key);
        }

        log.info("레디스에 결제 데이터 있는지 확인 : {}", redisValue);
        // Redis에서 필요한 정보 추출
        Long giftId = null;
        Long userId = null;
        Integer expectedAmount = null;

        String[] parts = redisValue.split(",");
        for (String part : parts) {
            if (part.startsWith("giftId:")) {
                giftId = Long.valueOf(part.split(":")[1]);
            }
            if (part.startsWith("userId:")) {
                userId = Long.valueOf(part.split(":")[1]);
            }
            if (part.startsWith("amount:")) {
                expectedAmount = Integer.parseInt(part.split(":")[1]);
            }
        }
        log.info("pos에서 결제한 금액 : {}", request.getPaymentAmount());
        log.info("레디스의 기프티콘 금액 : {}", expectedAmount);

        // 금액 검증. 이거 금액 달라도 보내지게 변경
       // boolean amountMatches = (expectedAmount != null && expectedAmount.equals(request.getPaymentAmount()));
       // log.info("토큰 금액과 결제 금액이 동일한지 확인 : {}", amountMatches);

        return TokenEqualResponseDto.builder()
                .result(true)
                .paymentToken(paymentToken)
                .paymentAmount(expectedAmount)
                .storeAccount(request.getStoreAccount())
                .userId(userId)
                .giftId(giftId)
                .build();
    }


}
