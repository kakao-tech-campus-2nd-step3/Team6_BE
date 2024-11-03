package supernova.whokie.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.s3.event.S3EventDto;
import supernova.whokie.s3.infrastructure.s3servicecaller.S3ServiceCaller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3ServiceCaller s3ServiceCaller;

    public void uploadFile(S3EventDto.Upload event) {
        try {
            BufferedImage bufferedImage = ImageIO.read(event.file().getInputStream());
            MultipartFile resizedImage = resizeImage(bufferedImage, bufferedImage.getWidth(), bufferedImage.getHeight(), event.width(), event.height());
            s3ServiceCaller.fileUpload(resizedImage, event.key());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSignedUrl(String key) {
        return s3ServiceCaller.getFileAsSignedUrl(key).toString();
    }

    public String createKey(String folderName, Long userId) {
        return folderName + "/" + userId + ".png";
    }

    private MultipartFile resizeImage(BufferedImage bufferedImage, int originalWidth, int originalHeight, int maxWidth, int maxHeight) throws IOException {
        double minRatio = calcRatio(originalWidth, originalHeight, maxWidth, maxHeight);
        int newWidth = (int) (originalWidth * minRatio);
        int newHeight = (int) (originalHeight * minRatio);
        BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newBufferedImage.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, newWidth, newHeight,null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBufferedImage, "png", baos);
        return new MockMultipartFile(
                "image",
                "resized_image.png",
                "image/png",
                baos.toByteArray()
        );
    }

    private double calcRatio(
            int originalWidth, int originalHeight, int maxWidth, int maxHeight
    ) {
        if(originalWidth < maxWidth && originalHeight < maxHeight) {
            return 1;
        }
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        return Math.min(widthRatio, heightRatio);
    }
}
