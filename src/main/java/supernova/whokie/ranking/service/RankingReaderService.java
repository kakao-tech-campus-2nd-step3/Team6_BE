package supernova.whokie.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingReaderService {
    private final RankingRepository rankingRepository;

    public List<Ranking> getTop3RankingByUserId(Long userId) {
        return rankingRepository.findTop3ByUsers_IdOrderByCountDesc(userId);
    }

    public List<Ranking> getTop3RankingByGroupId(Long groupId) {
        return rankingRepository.findTop3ByGroups_IdOrderByCountDesc(groupId);
    }
}
