package supernova.whokie.ranking.infrastructure.repoistory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UsersRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RankingRepositoryTest {

    @Autowired
    private RankingRepository rankingRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private GroupRepository groupRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("userId로 랭킹 조회")
    void findByUsers_IdOrderByCountDesc() {
        // given
        Users user = Users.builder().id(1L).name("host").email("host").point(1).age(1).kakaoId(1L)
            .gender(Gender.F).imageUrl("image").role(Role.USER).build();
        usersRepository.save(user);
        Groups group = Groups.builder().id(1L).groupName("group").description("test")
            .groupImageUrl("image").build();
        groupRepository.save(group);
        Ranking ranking1 = Ranking.builder().id(1L).question("q1").users(user).count(100)
            .groups(group).build();
        Ranking ranking2 = Ranking.builder().id(2L).question("q2").users(user).count(70)
            .groups(group).build();
        Ranking ranking3 = Ranking.builder().id(3L).question("q3").users(user).count(90)
            .groups(group).build();
        Ranking ranking4 = Ranking.builder().id(4L).question("q4").users(user).count(80)
            .groups(group).build();
        rankingRepository.saveAll(List.of(ranking1, ranking2, ranking3, ranking4));
        entityManager.flush();
        entityManager.clear();

        // when
        List<Ranking> actual = rankingRepository.findTop3ByUsers_IdOrderByCountDesc(user.getId());

        // then
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).getCount()).isEqualTo(ranking1.getCount());
        assertThat(actual.get(1).getCount()).isEqualTo(ranking3.getCount());
        assertThat(actual.get(2).getCount()).isEqualTo(ranking4.getCount());
    }
}