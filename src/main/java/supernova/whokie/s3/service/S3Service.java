package supernova.whokie.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.exception.FileTypeMismatchException;
import supernova.whokie.s3.event.S3EventDto;
import supernova.whokie.s3.infrastructure.s3servicecaller.S3ServiceCaller;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3ServiceCaller s3ServiceCaller;

    public void uploadFile(S3EventDto.Upload event) {
        s3ServiceCaller.fileUpload(event.file(), event.key());
    }

    public void uploadFile(MultipartFile file, String key) {
        s3ServiceCaller.fileUpload(file, key);
    }

    public String getSignedUrl(String key) {
        return s3ServiceCaller.getFileAsSignedUrl(key).toString();
    }

    public String createKey(String folderName, Long userId, MultipartFile file, String type) {
        validateFileType(file, type);
        return folderName + "/" + userId + "." + Constants.FILE_TYPE.get(type);
    }

    private void validateFileType(MultipartFile file, String fileType) {
        String actualFileType = file.getContentType();
        if (!Objects.equals(fileType, actualFileType)) {
            throw new FileTypeMismatchException("파일 형식이 잘못되었습니다.");
        }
    }
}
