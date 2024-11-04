package supernova.whokie.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.s3.event.S3EventDto;
import supernova.whokie.s3.infrastructure.s3servicecaller.S3ServiceCaller;
import supernova.whokie.s3.util.S3Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3ServiceCaller s3ServiceCaller;

    public void uploadFile(S3EventDto.Upload event) {
        try {
            BufferedImage bufferedImage = ImageIO.read(event.file().getInputStream());
            MultipartFile resizedImage = S3Util.resizeImage(bufferedImage, bufferedImage.getWidth(), bufferedImage.getHeight(), event.width(), event.height());
            s3ServiceCaller.fileUpload(resizedImage, event.key());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSignedUrl(String key) {
        return s3ServiceCaller.getFileAsSignedUrl(key).toString();
    }
}
