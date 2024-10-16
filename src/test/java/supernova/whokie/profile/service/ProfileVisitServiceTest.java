package supernova.whokie.profile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.profile.service.dto.ProfileModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "jwt.secret=abcd"
})
class ProfileVisitServiceTest {
    @Autowired
    private ProfileVisitService profileVisitService;
    @Autowired
    private ProfileVisitCountRepository profileVisitCountRepository;

    private ProfileVisitCount visitCount;

    @BeforeEach
    void setUp() {
        visitCount = createProfileVisitCount();
    }

    @Test
    @DisplayName("방문자 수 증가 테스트")
    void updateVisitCountTest() {
        // given
        Long hostId = visitCount.getHostId();
        String visitorIp = "visitorIp";
        int oldDailyVisited = visitCount.getDailyVisited();
        int oldTotalVisited = visitCount.getTotalVisited();

        // when
        ProfileModel.Visited actual = profileVisitService.visitProfile(hostId, visitorIp);

        // then
        assertAll(
                () -> assertThat(actual.todayVisited()).isEqualTo(oldDailyVisited + 1),
                () -> assertThat(actual.totalVisited()).isEqualTo(oldTotalVisited + 1),
                () -> assertThat(profileVisitService.checkVisited(hostId, visitorIp)).isTrue()
        );
    }

    private ProfileVisitCount createProfileVisitCount() {
        ProfileVisitCount visitCount = ProfileVisitCount.builder()
                .hostId(1L)
                .dailyVisited(0)
                .totalVisited(10)
                .build();
        profileVisitCountRepository.save(visitCount);
        return visitCount;
    }
}