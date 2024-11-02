package supernova.whokie.ranking.service;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group_member.service.GroupMemberReaderService;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.service.dto.RankingModel;
import supernova.whokie.user.Users;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
class RankingServiceTest {
    @InjectMocks
    private RankingService rankingService;
    @Mock
    private RankingReaderService rankingReaderService;
    @Mock
    private GroupMemberReaderService groupMemberReaderService;

    private List<Ranking> rankings;
    private Users user1;
    private Groups group1;

    @BeforeEach
    void setUp() {
        user1 = createUser();
        group1 = createGroup();
        rankings = createRankings();
    }

    @Test
    @DisplayName("특정 유저 랭킹 조회")
    void getUserRankingTest() {
        // given
        Users user = user1;
        Ranking ranking1 = rankings.get(0);
        Ranking ranking2 = rankings.get(1);
        Ranking ranking3 = rankings.get(2);
        given(rankingReaderService.getTop3RankingByUserId(user.getId()))
                .willReturn(rankings);

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
        Long userId = user1.getId();
        Long groupId = group1.getId();
        given(groupMemberReaderService.isGroupMemberExist(userId, groupId))
                .willReturn(false);

        // when
        // then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> rankingService.getGroupRanking(userId, groupId));
    }

    private Groups createGroup() {
        return Groups.builder().id(1L).build();
    }

    private List<Ranking> createRankings() {
        Ranking ranking1 = Ranking.builder().users(user1).count(100).groups(group1).build();
        Ranking ranking2 = Ranking.builder().users(user1).count(90).groups(group1).build();
        Ranking ranking3 = Ranking.builder().users(user1).count(80).groups(group1).build();
        return List.of(ranking1, ranking2, ranking3);
    }

    private Users createUser() {
        return Users.builder().id(1L).build();
    }
}