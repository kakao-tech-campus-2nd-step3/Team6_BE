package supernova.whokie.ranking.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.groupmember.service.GroupMemberReaderService;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.service.dto.RankingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingReaderService rankingReaderService;
    private final GroupMemberReaderService groupMemberReaderService;

    @Transactional(readOnly = true)
    public List<RankingModel.Rank> getUserRanking(Long userId) {
        List<Ranking> entities = rankingReaderService.getTop3RankingByUserId(userId);
        return IntStream.range(0, entities.size())
                .mapToObj(i -> RankingModel.Rank.from(entities.get(i), i + 1))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RankingModel.GroupRank> getGroupRanking(Long userId, Long groupId) {
        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }
     
        List<Ranking> rankings = rankingReaderService.getTop3RankingByGroupId(groupId);
        List<Map.Entry<String, Integer>> entries = getTop3UsersFromGroup(rankings);

        return IntStream.range(0, entries.size())
                .mapToObj(i -> RankingModel.GroupRank.from(entries.get(i), i + 1))
                .toList();
    }

    protected List<Map.Entry<String, Integer>> getTop3UsersFromGroup(List<Ranking> rankings) {
        Map<String, Integer> map = new HashMap<>();
        for (Ranking ranking : rankings) {
            if (!map.containsKey(ranking.getUsers().getName())) {
                map.put(ranking.getUsers().getName(), 0);
            }
            map.compute(ranking.getUsers().getName(), (k, value) -> value + ranking.getCount());
        }

        return map.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .toList();
    }
}
