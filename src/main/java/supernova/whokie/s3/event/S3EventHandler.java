package supernova.whokie.s3.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import supernova.whokie.s3.service.S3Service;

@Component
@RequiredArgsConstructor
public class S3EventHandler {
    private final S3Service s3Service;

    @Async
    @EventListener
    public void s3UploadListener(S3EventDto.Upload event) {
        s3Service.uploadFile(event);
    }
}
