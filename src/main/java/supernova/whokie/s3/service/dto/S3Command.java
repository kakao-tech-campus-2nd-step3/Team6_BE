package supernova.whokie.s3.service.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

public class S3Command {

    @Builder
    public record Upload(
            String folderName,
            Long id,
            MultipartFile file,
            String fileType
    ) {
        public static S3Command.Upload from(String folderName, Long id, MultipartFile file, String fileType) {
            return S3Command.Upload.builder()
                    .folderName(folderName)
                    .id(id)
                    .file(file)
                    .fileType(fileType)
                    .build();
        }
    }
}
