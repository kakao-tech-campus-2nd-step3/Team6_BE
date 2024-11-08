package supernova.whokie.ranking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.group.Groups;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.ranking.service.dto.RankingModel;
import supernova.whokie.user.Users;

@ExtendWith(MockitoExtension.class)
class RankingReaderServiceTest {

    @InjectMocks
    private RankingReaderService rankingReaderService;
    @Mock
    private RankingRepository rankingRepository;

    private List<Ranking> rankings;
    private List<Users> users;
    private Groups group1;

    @BeforeEach
    void setUp() {
        users = createUser();
        group1 = createGroup();
        rankings = createRankings();
    }

    @Test
    @DisplayName("그룹 내에서 count가 높은 3명 뽑기 테스트")
    void getTop3UsersFromGroupTest() {
        // given
        List<Ranking> rankingList = rankings;
        Groups group = group1;
        int finalCount =
            rankingList.get(0).getCount() + rankingList.get(1).getCount() + rankingList.get(2)
                .getCount();
        int finalCount1 = rankingList.get(3).getCount();
        given(rankingRepository.findAllByGroupIdFetchJoinUsers(group.getId()))
            .willReturn(rankingList);

        // when
        RankingModel.Top3RankingEntries actuals = rankingReaderService.getTop3UsersFromGroupByGroupId(
            group.getId());

        // then
        assertAll(
            () -> assertThat(actuals.entries()).hasSize(2),
            () -> assertThat(actuals.entries().get(0).getKey()).isEqualTo(users.get(0).getId()),
            () -> assertThat(actuals.entries().get(0).getValue()).isEqualTo(finalCount),
            () -> assertThat(actuals.entries().get(1).getKey()).isEqualTo(users.get(1).getId()),
            () -> assertThat(actuals.entries().get(1).getValue()).isEqualTo(finalCount1)
        );
    }

    private Groups createGroup() {
        return Groups.builder().id(1L).build();
    }

    private List<Ranking> createRankings() {
        Ranking ranking1 = Ranking.builder().users(users.get(0)).count(100).groups(group1).build();
        Ranking ranking2 = Ranking.builder().users(users.get(0)).count(90).groups(group1).build();
        Ranking ranking3 = Ranking.builder().users(users.get(0)).count(80).groups(group1).build();
        Ranking ranking4 = Ranking.builder().users(users.get(1)).count(80).groups(group1).build();
        return List.of(ranking1, ranking2, ranking3, ranking4);
    }

    private List<Users> createUser() {
        Users user1 = Users.builder().id(1L).name("name1").build();
        Users user2 = Users.builder().id(2L).name("name2").build();
        return List.of(user1, user2);
    }
}