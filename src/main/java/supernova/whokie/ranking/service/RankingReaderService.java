package supernova.whokie.ranking.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.ranking.service.dto.RankingModel;

@Service
@RequiredArgsConstructor
public class RankingReaderService {

    private final RankingRepository rankingRepository;

    @Transactional(readOnly = true)
    public List<Ranking> getTop3RankingByUserId(Long userId) {
        return rankingRepository.findTop3ByUsers_IdOrderByCountDesc(userId);
    }

    @Transactional(readOnly = true)
    public RankingModel.Top3RankingEntries getTop3UsersFromGroupByGroupId(Long groupId) {
        List<Ranking> rankings = rankingRepository.findAllByGroupIdFetchJoinUsers(groupId);
        Map<Long, Integer> map = new HashMap<>();
        for (Ranking ranking : rankings) {
            Long userId = ranking.getUsers().getId();
            map.putIfAbsent(userId, 0);
            map.merge(userId, ranking.getCount(), Integer::sum);
        }

        return RankingModel.Top3RankingEntries.from(map);
    }
}
