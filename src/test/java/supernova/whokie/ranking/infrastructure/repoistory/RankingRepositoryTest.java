package supernova.whokie.ranking.infrastructure.repoistory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupRepository;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RankingRepositoryTest {

    @Autowired
    private RankingRepository rankingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Users user1;
    private Groups group1;
    private List<Ranking> rankings;

    @BeforeEach
    void setUp() {
        user1 = createUser();
        group1 = createGroup();
        rankings = createRankings();
    }

    @Test
    @DisplayName("userId로 랭킹 조회")
    void findByUsers_IdOrderByCountDesc() {
        // given
        Users user = user1;
        List<Ranking> rankingList = rankings;

        // when
        List<Ranking> actual = rankingRepository.findTop3ByUsers_IdOrderByCountDesc(user.getId());

        // then
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).getCount()).isEqualTo(rankingList.get(0).getCount());
        assertThat(actual.get(1).getCount()).isEqualTo(rankingList.get(2).getCount());
        assertThat(actual.get(2).getCount()).isEqualTo(rankingList.get(3).getCount());
    }

    private Users createUser() {
        Users user = Users.builder().id(1L).name("host").email("host").point(1).age(1).kakaoId(1L)
                .gender(Gender.F).imageUrl("image").role(Role.USER).build();
        return userRepository.save(user);
    }

    private Groups createGroup() {
        Groups group = Groups.builder().id(1L).groupName("group").description("test")
                .groupImageUrl("image").build();
        return groupRepository.save(group);
    }

    private List<Ranking> createRankings() {
        Ranking ranking1 = Ranking.builder().id(1L).question("q1").users(user1).count(100)
                .groups(group1).build();
        Ranking ranking2 = Ranking.builder().id(2L).question("q2").users(user1).count(70)
                .groups(group1).build();
        Ranking ranking3 = Ranking.builder().id(3L).question("q3").users(user1).count(90)
                .groups(group1).build();
        Ranking ranking4 = Ranking.builder().id(4L).question("q4").users(user1).count(80)
                .groups(group1).build();
        return rankingRepository.saveAll(List.of(ranking1, ranking2, ranking3, ranking4));
    }
}