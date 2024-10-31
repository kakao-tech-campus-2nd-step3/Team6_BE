package supernova.whokie.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.cloud.aws.s3")
public record AwsS3Properties(
        String bucket
) {

}
