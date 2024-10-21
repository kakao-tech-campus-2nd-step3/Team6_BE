package supernova.whokie.ranking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.group.Groups;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.ranking.service.dto.RankingModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "jwt.secret=abcd"
})
class RankingServiceTest {
    @InjectMocks
    private RankingService rankingService;
    @Mock
    private RankingRepository rankingRepository;

    private List<Ranking> rankings;

    @BeforeEach
    void setUp() {
        rankings = createRankings();
    }

    @Test
    @DisplayName("특정 유저 랭킹 조회")
    void getUserRankingTest() {
        // given
        Ranking ranking1 = rankings.get(0);
        Ranking ranking2 = rankings.get(1);
        Ranking ranking3 = rankings.get(2);
        given(rankingRepository.findTop3ByUsers_IdOrderByCountDesc(any()))
                .willReturn(rankings);

        // when
        List<RankingModel.Rank> actual = rankingService.getUserRanking(1L);

        // then
        assertAll(
            () -> assertThat(actual).hasSize(3),
            () -> assertThat(actual.get(0).count()).isEqualTo(ranking1.getCount()),
            () -> assertThat(actual.get(1).count()).isEqualTo(ranking2.getCount()),
            () -> assertThat(actual.get(2).count()).isEqualTo(ranking3.getCount())
        );
    }

    private List<Ranking> createRankings() {
        Groups group = Groups.builder().build();
        Ranking ranking1 = Ranking.builder().count(100).groups(group).build();
        Ranking ranking2 = Ranking.builder().count(90).groups(group).build();
        Ranking ranking3 = Ranking.builder().count(80).groups(group).build();
        return List.of(ranking1, ranking2, ranking3);
    }
}