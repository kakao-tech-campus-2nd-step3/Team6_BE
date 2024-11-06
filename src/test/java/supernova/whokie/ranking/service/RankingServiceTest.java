package supernova.whokie.ranking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.groupmember.service.GroupMemberReaderService;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.service.dto.RankingModel;
import supernova.whokie.user.Users;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @InjectMocks
    private RankingService rankingService;
    @Mock
    private RankingReaderService rankingReaderService;
    @Mock
    private GroupMemberReaderService groupMemberReaderService;

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
    @DisplayName("특정 유저 랭킹 조회")
    void getUserRankingTest() {
        // given
        Users user = users.get(0);
        Ranking ranking1 = rankings.get(0);
        Ranking ranking2 = rankings.get(1);
        Ranking ranking3 = rankings.get(2);
        List<Ranking> rankingList = List.of(ranking1, ranking2, ranking3);
        given(rankingReaderService.getTop3RankingByUserId(user.getId()))
                .willReturn(rankingList);

        // when
        List<RankingModel.Rank> actual = rankingService.getUserRanking(user.getId());

        // then
        assertAll(
            () -> assertThat(actual).hasSize(3),
            () -> assertThat(actual.get(0).count()).isEqualTo(ranking1.getCount()),
            () -> assertThat(actual.get(1).count()).isEqualTo(ranking2.getCount()),
            () -> assertThat(actual.get(2).count()).isEqualTo(ranking3.getCount())
        );
    }

    @Test
    @DisplayName("특정 그룹 랭킹 조회 실패")
    void getGroupRankingTest() {
        // given
        Long userId = users.get(0).getId();
        Long groupId = group1.getId();
        given(groupMemberReaderService.isGroupMemberExist(userId, groupId))
                .willReturn(false);

        // when
        // then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> rankingService.getGroupRanking(userId, groupId));
    }

    @Test
    @DisplayName("그룹 내에서 count가 높은 3명 뽑기 테스트")
    void getTop3UsersFromGroupTest() {
        // given
        List<Ranking> rankingList = rankings;
        int finalCount = rankingList.get(0).getCount() + rankingList.get(1).getCount() + rankingList.get(2).getCount();
        int finalCount1 = rankingList.get(3).getCount();

        // when
        List<Map.Entry<String, Integer>> actuals = rankingService.getTop3UsersFromGroup(rankingList);

        assertAll(
                () -> assertThat(actuals).hasSize(2),
                () -> assertThat(actuals.get(0).getKey()).isEqualTo(users.get(0).getName()),
                () -> assertThat(actuals.get(0).getValue()).isEqualTo(finalCount),
                () -> assertThat(actuals.get(1).getKey()).isEqualTo(users.get(1).getName()),
                () -> assertThat(actuals.get(1).getValue()).isEqualTo(finalCount1)
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
        Users user1 =  Users.builder().id(1L).name("name1").build();
        Users user2 =  Users.builder().id(2L).name("name2").build();
        return List.of(user1, user2);
    }
}