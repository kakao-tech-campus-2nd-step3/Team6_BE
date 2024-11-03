package supernova.whokie.ranking.service.dto;

import lombok.Builder;
import supernova.whokie.ranking.Ranking;

public class RankingModel {

    @Builder
    public record Rank(
            Long rankingId,
            String question,
            int rank,
            int count,
            String groupName
    ) {
        public static RankingModel.Rank from(Ranking entity, int rank) {
            return RankingModel.Rank.builder()
                    .rankingId(entity.getId())
                    .question(entity.getQuestion())
                    .rank(rank)
                    .count(entity.getCount())
                    .groupName(entity.getGroups().getGroupName())
                    .build();
        }
    }
}
