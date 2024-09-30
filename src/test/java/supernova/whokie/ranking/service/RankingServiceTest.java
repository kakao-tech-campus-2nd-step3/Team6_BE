package supernova.whokie.ranking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.group.Groups;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.ranking.service.dto.RankingModel;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RankingServiceTest {
    @Autowired
    private RankingService rankingService;
    @MockBean
    private RankingRepository rankingRepository;

    @Test
    @DisplayName("특정 유저 랭킹 조회")
    void getUserRankingTest() {
        // given
        Groups group = Groups.builder().build();
        Ranking ranking1 = Ranking.builder().count(100).groups(group).build();
        Ranking ranking2 = Ranking.builder().count(90).groups(group).build();
        Ranking ranking3 = Ranking.builder().count(80).groups(group).build();
        List<Ranking> entities = List.of(ranking1, ranking2, ranking3);
        given(rankingRepository.findTop3ByUsers_IdOrderByCountDesc(any()))
                .willReturn(entities);

        // when
        List<RankingModel.Rank> actual = rankingService.getUserRanking(1L);

        // then
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).count()).isEqualTo(ranking1.getCount());
        assertThat(actual.get(1).count()).isEqualTo(ranking2.getCount());
        assertThat(actual.get(2).count()).isEqualTo(ranking3.getCount());
    }
}