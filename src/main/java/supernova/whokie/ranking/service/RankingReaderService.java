package supernova.whokie.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingReaderService {
    private final RankingRepository rankingRepository;

    @Transactional(readOnly = true)
    public List<Ranking> getTop3RankingByUserId(Long userId) {
        return rankingRepository.findTop3ByUsers_IdOrderByCountDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Ranking> getTop3RankingByGroupId(Long groupId) {
        return rankingRepository.findAllByGroupIdFetchJoinUsers(groupId);
    }
}
