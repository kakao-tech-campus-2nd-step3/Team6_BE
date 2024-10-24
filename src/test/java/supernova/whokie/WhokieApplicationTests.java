package supernova.whokie;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=abcd"

})
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
class WhokieApplicationTests {

    @Test
    void contextLoads() {
    }

}
