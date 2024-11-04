package supernova.whokie.s3.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class S3Util {
    private static final String S3_FILE_TYPE = "png";
    private static final String S3_KEY_FORMAT = "%s/%d.%s";

    private S3Util() {}

    public static String generateS3Key(String folderName, Long userId) {
        return String.format(S3_KEY_FORMAT, folderName, userId, S3_FILE_TYPE);
    }
    public static MultipartFile resizeImage(BufferedImage bufferedImage, int originalWidth, int originalHeight, int maxWidth, int maxHeight) throws IOException {
        double minRatio = calcRatio(originalWidth, originalHeight, maxWidth, maxHeight);
        int newWidth = (int) (originalWidth * minRatio);
        int newHeight = (int) (originalHeight * minRatio);
        BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newBufferedImage.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, newWidth, newHeight,null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBufferedImage, S3_FILE_TYPE, baos);
        return new MockMultipartFile(
                "image",
                "resized_image." + S3_FILE_TYPE,
                "image/" + S3_FILE_TYPE,
                baos.toByteArray()
        );
    }

    private static double calcRatio(
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
