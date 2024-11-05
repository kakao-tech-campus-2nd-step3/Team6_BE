package supernova.whokie.alarm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import supernova.whokie.alarm.event.AlarmEventDto;
import supernova.whokie.global.constants.Constants;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(Constants.SSE_TIMEOUT);
        emitters.put(userId, emitter);

        // SseEmitter가 complate 됐을 경우
        emitter.onCompletion(() -> emitters.remove(userId));

        // SseEmitter가 timeout 됐을 경우
        emitter.onTimeout(emitter::complete);

        // Error 발생
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }

    public void disconnect(Long userId) {
        emitters.remove(userId);
    }

    public void alarmToConnectedUser(AlarmEventDto.Alarm event) {
        SseEmitter emitter = emitters.get(event.userId());
        try {
            if (emitter != null) {
                emitter.send(event.question());
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
            emitters.remove(event.userId());
        }
    }
}
