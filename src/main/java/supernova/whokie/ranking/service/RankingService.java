package supernova.whokie.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.groupmember.service.GroupMemberReaderService;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.service.dto.RankingModel;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingReaderService rankingReaderService;
    private final GroupMemberReaderService groupMemberReaderService;


    public List<RankingModel.Rank> getUserRanking(Long userId) {
        List<Ranking> entities = rankingReaderService.getTop3RankingByUserId(userId);
        return IntStream.range(0, entities.size())
                .mapToObj(i -> RankingModel.Rank.from(entities.get(i), i + 1))
                .toList();
    }

    public List<RankingModel.Rank> getGroupRanking(Long userId, Long groupId) {
        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }

        List<Ranking> entities = rankingReaderService.getTop3RankingByGroupId(groupId);
        return IntStream.range(0, entities.size())
                .mapToObj(i -> RankingModel.Rank.from(entities.get(i), i + 1))
                .toList();
    }
}
