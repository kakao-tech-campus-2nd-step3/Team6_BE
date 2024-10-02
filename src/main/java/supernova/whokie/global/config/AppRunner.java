package supernova.whokie.global.config;

import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    private final Environment environment;

    public AppRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("===================활성화된 프로필===================");
        System.out.println("Active profiles : " + Arrays.toString(environment.getActiveProfiles()));
        System.out.println("db url : " + environment.getProperty("spring.datasource.url"));
    }
}