package supernova.whokie.ranking.infrastructure.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.ranking.Ranking;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findTop3ByUsers_IdOrderByCountDesc(Long userId);
}
