package com.example.ddo_pay.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class SseService {
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    // 요청 주기는 서버가 .send() 호출할 때만 클라이언트로 메시지 전달됨(클라이언트가 주기적으로 요청하지 않음)
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 타임아웃 설정
        emitter.onCompletion(() -> emitterMap.remove(userId)); // 정상 종료 시 동작
        emitter.onTimeout(() -> emitterMap.remove(userId)); // 타임아웃 시 동작

        emitterMap.put(userId, emitter);
        return emitter;
    }

    public void sendToUser(String userId, String message) {
        SseEmitter emitter = emitterMap.get(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("payment-result").data(message));
            } catch(IOException e) {
                emitter.completeWithError(e);
                emitterMap.remove(userId);
            }
        }
    }
}
