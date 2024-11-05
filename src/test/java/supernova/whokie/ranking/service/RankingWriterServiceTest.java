package supernova.whokie.ranking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupRepository;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(RankingWriterService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RankingWriterServiceTest {
    @Autowired
    private RankingRepository rankingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private RankingWriterService rankingWriterService;
    @PersistenceContext
    EntityManager entityManager;

    private List<Users> users;
    private List<Groups> groups;
    private List<Ranking> rankings;

    @BeforeEach
    void setUp() {
        users = createUsers();
        groups = createGroups();
        rankings = createRankings();
    }

    @Test
    @DisplayName("Ranking Count 증가 테스트")
    void increaseRankingCountByUserIdTest() {
        // given
        Ranking ranking = rankings.get(0);
        int originalCount = ranking.getCount();
        Users user = ranking.getUsers();
        String question = ranking.getQuestion();
        Groups group1 = ranking.getGroups();

        // when
        rankingWriterService.increaseRankingCountByUserAndQuestionAndGroups(user, question, group1);
        entityManager.flush();
        entityManager.clear();
        Ranking actual = rankingRepository.findByUsersAndQuestionAndGroups(user, question, group1).orElse(null);

        // then
        assertThat(actual.getCount()).isEqualTo(originalCount + 1);
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder()
                .id(1L)
                .name("host")
                .email("host")
                .point(1)
                .age(1)
                .kakaoId(1L)
                .gender(Gender.F)
                .imageUrl("image")
                .role(Role.USER)
                .build();
        Users user2 = Users.builder()
                .id(2L)
                .name("host2")
                .email("host2")
                .point(2)
                .age(2)
                .kakaoId(2L)
                .gender(Gender.F)
                .imageUrl("image2")
                .role(Role.USER)
                .build();
        return userRepository.saveAll(List.of(user1, user2));
    }

    private List<Groups> createGroups() {
        Groups group1 = Groups.builder()
                .id(2L)
                .groupName("group1")
                .description("test")
                .groupImageUrl("image")
                .build();
        Groups group2 = Groups.builder()
                .id(3L)
                .groupName("group2")
                .description("test2")
                .groupImageUrl("image2")
                .build();
        return groupRepository.saveAll(List.of(group1, group2));
    }

    private List<Ranking> createRankings() {
        Ranking ranking1 = Ranking.builder().id(1L).question("q1").users(users.get(0)).count(100)
                .groups(groups.get(0)).build();
        Ranking ranking2 = Ranking.builder().id(2L).question("q2").users(users.get(0)).count(70)
                .groups(groups.get(0)).build();
        Ranking ranking3 = Ranking.builder().id(3L).question("q3").users(users.get(0)).count(90)
                .groups(groups.get(1)).build();
        Ranking ranking4 = Ranking.builder().id(4L).question("q4").users(users.get(1)).count(80)
                .groups(groups.get(1)).build();

        return rankingRepository.saveAll(List.of(ranking1, ranking2, ranking3, ranking4));
    }
}