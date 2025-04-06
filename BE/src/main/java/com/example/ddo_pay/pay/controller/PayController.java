package com.example.ddo_pay.pay.controller;

import com.example.ddo_pay.common.exception.CustomException;
import com.example.ddo_pay.common.response.Response;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.common.util.SecurityUtil;
import com.example.ddo_pay.pay.dto.bank_request.PosRequest;
import com.example.ddo_pay.pay.dto.request.*;
import com.example.ddo_pay.pay.dto.bank_request.TokenEqualResponseDto;
import com.example.ddo_pay.pay.dto.response.GetAccountResponse;
import com.example.ddo_pay.pay.dto.response.GetBalanceResponse;
import com.example.ddo_pay.pay.dto.response.GetHistoryListResponse;
import com.example.ddo_pay.pay.dto.response.GetPointResponse;
import com.example.ddo_pay.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    // 유효 계좌 인증
    @PostMapping("/account/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody AccountVerifyRequest request) {
        Long userId = SecurityUtil.getUserId();
        String financeCode = payService.verifyAccount(userId, request);

        ResponseCode responseCode = switch (financeCode) {
            case "H0000" -> ResponseCode.SUCCESS_VERIFY_ACCOUNT;
            case "A1003" -> ResponseCode.INVALID_ACCOUNT;
            case "ERR_API" -> ResponseCode.FINANCE_API_ERROR;
            case "ERR_PARSING" -> ResponseCode.FINANCE_PARSING_ERROR;
            default -> ResponseCode.UNKNOWN_ERROR;
        };

        return new ResponseEntity<>(Response.create(responseCode, null), responseCode.getHttpStatus());
    }

    // 비밀번호 등록 및 또페이 생성
    @PostMapping("/password")
    public ResponseEntity<?> registerPassword(@RequestBody RegisterPasswordRequest request) {
        Long userId = SecurityUtil.getUserId();
        payService.registerPayPassword(userId, request);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_REGISTER_DDOPAY, null));
    }

    // 본인 계좌 인증 후 계좌 연동
    @PostMapping("/account")
    public ResponseEntity<?> registerAccount(@RequestBody RegisterAccountRequest request) {
        Long userId = SecurityUtil.getUserId();
        payService.registerAccount(userId, request);

        return ResponseEntity.ok(
                Response.create(ResponseCode.SUCCESS_REGISTER_ACCOUNT, null)
        );
    }


    // 잔고 조회
    @GetMapping("/balance")
    public ResponseEntity<?> selectBalance() {
        Long userId = SecurityUtil.getUserId();
        GetBalanceResponse response = payService.selectBalance(userId);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_BALANCE_CHECK, response));
    }


    // 포인트 조회
    @GetMapping("/point")
    public ResponseEntity<?> selectPoint() {
        Long userId = SecurityUtil.getUserId();
        GetPointResponse response = payService.selectPoint(userId);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_POINT_CHECK, response));
    }

    // 연결된 계좌 조회
    @GetMapping("/account")
    public ResponseEntity<?> selectAccountList() {
        Long userId = SecurityUtil.getUserId();
        List<GetAccountResponse> dtos = payService.selectAccountList(userId);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_ACCOUNT_CHECK, dtos));
    }

    // 또페이 충전
    @PostMapping("/charge")
    public ResponseEntity<?> chargeDdoPay(@RequestBody ChargeDdoPayRequest request) {
        Long userId = SecurityUtil.getUserId();
        payService.transferDdoPay(userId, request);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_BALANCE_CHARGE, "충전성공"));
    }

    // POS에서 토큰, 결제 금액, 가게 계좌 받기
    @PostMapping("/pos")
    public ResponseEntity<?> posPayment(@RequestBody PosRequest request) {
        try {
            // request.getPaymentToken() 값을 들고와서 Redis의 토큰값과 비교한다.
            TokenEqualResponseDto matchedDto = payService.comparePaymentToken(request);

            log.info(matchedDto.getPaymentToken());
            log.info(matchedDto.getResult().toString());
            log.info(String.valueOf(matchedDto.getPaymentAmount()));

            // 같다면, 다음 로직 실행(계좌 이체 요청(feignclient) -> 깊티 상태 변경(Service) -> 성공 응답(SSE))
            payService.posPayment(matchedDto);

            log.info("POS 결제 요청 성공 처리 - 토큰: {}, 금액: {}, 가맹점 계좌: {}",
                    request.getPaymentToken(),
                    request.getPaymentAmount(),
                    request.getStoreAccount());

            return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_PAYMENT, "결제가 성공적으로 처리되었습니다."));
        } catch (CustomException e) {
            log.error("POS 결제 요청 실패 - 토큰: {}, 에러: {}",
                    request.getPaymentToken(),
                    e.getMessage());

            // CustomException에서 응답 코드를 가져오는 방식에 따라 수정
            ResponseCode errorCode = ResponseCode.UNKNOWN_ERROR; // 기본값

            // CustomException에서 응답 코드를 가져오는 방법에 따라 아래 코드 변경
            // 예: e.getCode() 또는 e.getResponseCode() 등

            return new ResponseEntity<>(
                    Response.create(errorCode, null),
                    errorCode.getHttpStatus()
            );
        } catch (Exception e) {
            log.error("POS 결제 요청 처리 중 예상치 못한 오류 발생", e);

            return new ResponseEntity<>(
                    Response.create(ResponseCode.INTERNAL_SERVER_ERROR, null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 또페이 결제 내역 조회
    @GetMapping("/history")
    public ResponseEntity<?> selectHistory(@RequestParam(name = "history_type") String historyType) {
        Long userId = SecurityUtil.getUserId();
        SelectHistoryRequest request = new SelectHistoryRequest(historyType);
        List<GetHistoryListResponse> responses = payService.selectHistoryList(userId, request);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_SELECT_HISTORY, responses));
    }




}
