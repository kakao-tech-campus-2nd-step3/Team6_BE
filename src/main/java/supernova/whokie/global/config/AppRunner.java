package supernova.whokie.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class AppRunner implements ApplicationRunner {

    private final Environment environment;

    public AppRunner(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    @Override
    public void run(ApplicationArguments args) {
        log.info("=================== 활성화된 프로필 ===================");
        log.info("Active profiles : {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("db url : {}", environment.getProperty("spring.datasource.url"));
        log.info("db username : {}", environment.getProperty("spring.datasource.username"));
        log.info("secret key : {}", environment.getProperty("jwt.secret"));
    }
}