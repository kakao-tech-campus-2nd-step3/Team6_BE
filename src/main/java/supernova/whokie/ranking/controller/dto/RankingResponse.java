package supernova.whokie.ranking.controller.dto;

import lombok.Builder;
import supernova.whokie.ranking.service.dto.RankingModel;

import java.util.List;

public class RankingResponse {

    @Builder
    public record Ranks(
            List<Rank> ranks
    ) {
        public static RankingResponse.Ranks from(List<RankingModel.Rank> models) {
            return Ranks.builder()
                    .ranks(models.stream().map(RankingResponse.Rank::from).toList())
                    .build();
        }
    }

    @Builder
    public record Rank(
            Long rankingId,
            String question,
            int rank,
            int count,
            String groupName
    ) {
        public static RankingResponse.Rank from(RankingModel.Rank model) {
            return RankingResponse.Rank.builder()
                    .rankingId(model.rankingId())
                    .question(model.question())
                    .rank(model.rank())
                    .count(model.count())
                    .groupName(model.groupName())
                    .build();
        }
    }
}
