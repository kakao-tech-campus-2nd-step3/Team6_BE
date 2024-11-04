package supernova.whokie.s3.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class S3UtilTest {

    @Test
    @DisplayName("키 생성 테스트")
    void generateS3KeyTest() {
        // given
        String folderName = "folder";
        Long userId = 1L;
        String expected = folderName + "/" + userId + ".png";

        // when
        String actual = S3Util.generateS3Key(folderName, userId);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}