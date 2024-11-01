package supernova.whokie.ranking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.group.Groups;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.user.Users;

@Service
@AllArgsConstructor
public class RankingWriterService {
    private RankingRepository rankingRepository;

    @Transactional
    public void updateRanking(String question, Users user, Groups group) {
        if (!rankingRepository.existsByUsers_IdAndQuestion(user.getId(), question)) {
            Ranking ranking = Ranking.builder().question(question).count(Constants.DEFAULT_RANKING_COUNT).users(user).groups(group).build();
            rankingRepository.save(ranking);
            return;
        }

        Ranking ranking = rankingRepository.findByUsers_IdAndQuestion(user.getId(), question);
        ranking.updateCount();
    }
}
