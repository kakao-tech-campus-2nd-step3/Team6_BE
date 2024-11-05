package supernova.whokie.ranking.infrastructure.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.group.Groups;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.user.Users;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    List<Ranking> findTop3ByUsers_IdOrderByCountDesc(Long userId);

    List<Ranking> findTop3ByGroups_IdOrderByCountDesc(Long groupId);

    Optional<Ranking> findByUsersAndQuestionAndGroups(Users users, String question, Groups groups);
}
