package supernova.whokie.profile.infrastructure.downloader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class ImageDownloader {
    public MultipartFile downloadImageAsMultipartFile(String imageUrl) throws Exception {
        URI uri = new URI(imageUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 이미지 데이터를 읽어들임
        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // 이미지 데이터를 바이트 배열로 저장
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 바이트 배열을 MultipartFile로 변환
            return new CustomMultipartFile(
                    Constants.DEFAULT_PROFILE_IMAGE_FILENAME,   // 파일명
                    outputStream.toByteArray(),   // 바이트 데이터
                    connection.getContentType()   // 파일 형식
            );
        }
    }
}
