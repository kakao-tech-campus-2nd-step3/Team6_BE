package supernova.whokie.s3.infrastructure.s3servicecaller;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.property.AwsS3Properties;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3ServiceCaller {

    private final AwsS3Properties awsS3Properties;
    private final S3Template s3Template;

    public void fileUpload(MultipartFile file, String key)  {
        try {
            s3Template.upload(
                    awsS3Properties.bucket(),
                    key,
                    file.getInputStream()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
