package supernova.whokie.ranking.controller.dto;

import lombok.Builder;

import java.util.List;

public class RankingResponse {

    @Builder
    public record Ranks(
            List<Rank> ranks
    ) {
    }

    @Builder
    public record Rank(
            Long rankingId,
            Long questionId,
            String question,
            int rank,
            int count,
            String groupName
    ) {

    }
}
