package supernova.whokie.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.exception.FileTypeMismatchException;
import supernova.whokie.s3.infrastructure.s3servicecaller.S3ServiceCaller;
import supernova.whokie.s3.service.dto.S3Command;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3ServiceCaller s3ServiceCaller;

    public String uploadFile(S3Command.Upload command) {
        String key = createKey(command);

        validateFileType(command.file(), command.fileType());
        s3ServiceCaller.fileUpload(command.file(), key);
        return key;
    }

    public String getSignedUrl(String key) {
        return s3ServiceCaller.getFileAsSignedUrl(key).toString();
    }

    private String createKey(S3Command.Upload command) {
        validateFileType(command.file(), command.fileType());
        return command.folderName() + "/" + command.id() + "." + Constants.FILE_TYPE.get(command.fileType());
    }

    private void validateFileType(MultipartFile file, String fileType) {
        String actualFileType = file.getContentType();
        if (!Objects.equals(fileType, actualFileType)) {
            throw new FileTypeMismatchException("파일 형식이 잘못되었습니다.");
        }
    }
}
