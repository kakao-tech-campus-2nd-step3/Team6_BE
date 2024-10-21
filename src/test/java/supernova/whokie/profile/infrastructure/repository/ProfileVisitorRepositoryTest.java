package supernova.whokie.profile.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.global.config.JpaConfig;
import supernova.whokie.profile.ProfileVisitor;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileVisitorRepositoryTest {

    @Autowired
    private ProfileVisitorRepository profileVisitorRepository;

    private ProfileVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = createProfileVisitor();
    }

    @Test
    @DisplayName("날짜로 방문자 로그 확인")
    void existsByHostIdAndVisitorIpAndModifiedAtBetweenTest() {
        // given
        Long hostId = visitor.getHostId();
        String visitorIp = visitor.getVisitorIp();
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay(); // 오늘 00:00
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999999); // 오늘 23:59:59.999999

        // when
        boolean actual = profileVisitorRepository.existsByHostIdAndVisitorIpAndModifiedAtBetween(hostId, visitorIp, startOfDay, endOfDay);

        // then
        assertThat(actual).isTrue();
    }

    private ProfileVisitor createProfileVisitor() {
        ProfileVisitor visitor = ProfileVisitor.builder()
                .visitorIp("visitorIp")
                .hostId(1L)
                .build();

        profileVisitorRepository.save(visitor);
        return visitor;
    }
}