package supernova.whokie.ranking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.ranking.controller.dto.RankingResponse;

import java.util.List;

@RestController
@RequestMapping("/api/profile/ranking")
public class RankingController {

    @GetMapping("/{user-Id}")
    public RankingResponse.Ranks getProfileRanking(
            @PathVariable("user-Id") String userId
    ) {
        return RankingResponse.Ranks.builder()
                .ranks(List.of(new RankingResponse.Rank(1L, 12L, "quest1", 1, 123, "GLOBAL"),
                        new RankingResponse.Rank(2L, 13L, "quest2", 2, 12, "group1"),
                        new RankingResponse.Rank(3L, 14L, "quest3", 3, 1, "group1")))
                .build();
    }

}
