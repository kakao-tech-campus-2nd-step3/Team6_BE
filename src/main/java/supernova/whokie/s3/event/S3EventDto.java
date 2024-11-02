package supernova.whokie.s3.event;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

public class S3EventDto {
    @Builder
    public record Upload(
            MultipartFile file,
            String key
    ) {
        public static S3EventDto.Upload toDto(MultipartFile file, String key) {
            return S3EventDto.Upload.builder()
                    .file(file)
                    .key(key)
                    .build();
        }
    }
}
