package supernova.whokie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class WhokieApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhokieApplication.class, args);
    }

}
