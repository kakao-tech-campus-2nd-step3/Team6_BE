package supernova.whokie.ranking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.ranking.service.dto.RankingModel;

import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class RankingService {
    private final RankingRepository rankingRepository;

    public List<RankingModel.Rank> getUserRanking(Long userId) {
        List<Ranking> entities = rankingRepository.findTop3ByUsers_IdOrderByCountDesc(userId);
        return IntStream.range(0, entities.size())
                .mapToObj(i -> RankingModel.Rank.from(entities.get(i), i + 1))
                .toList();
    }
}
