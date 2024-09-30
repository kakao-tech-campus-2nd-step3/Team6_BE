package supernova.whokie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=default",
    "jwt.secret=abcd"
})
class WhokieApplicationTests {

    @Test
    void contextLoads() {
    }

}
