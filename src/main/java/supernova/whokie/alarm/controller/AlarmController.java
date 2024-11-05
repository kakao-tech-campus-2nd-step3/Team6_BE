package supernova.whokie.alarm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import supernova.whokie.alarm.event.AlarmEventDto;
import supernova.whokie.alarm.service.AlarmService;
import supernova.whokie.global.annotation.Authenticate;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    private final ApplicationEventPublisher publisher;

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents(@Authenticate Long userId) {
        return alarmService.connect(userId);
    }

    @PostMapping("/disconnect")
    public void disconnect(@Authenticate Long userId) {
        alarmService.disconnect(userId);
    }

    @PostMapping("/test")
    public ResponseEntity<Void> test(@Authenticate Long userId) {
        publisher.publishEvent(AlarmEventDto.Alarm.toDto(userId, "test"));
        return ResponseEntity.ok().build();
    }
}
