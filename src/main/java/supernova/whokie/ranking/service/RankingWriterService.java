package supernova.whokie.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.group.Groups;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.user.Users;

@Service
@RequiredArgsConstructor
public class RankingWriterService {
    private final RankingRepository rankingRepository;

    @Transactional
    public void save(Ranking ranking) {
        rankingRepository.save(ranking);
    }

    @Transactional
    public Ranking createRanking(Users user, String question, Groups groups) {
        Ranking ranking = Ranking.builder()
                .question(question)
                .count(Constants.DEFAULT_RANKING_COUNT)
                .users(user)
                .groups(groups)
                .build();
        return rankingRepository.save(ranking);
    }

    @Transactional
    public void increaseRankingCountByUserAndQuestionAndGroups(Users user, String question, Groups group) {
        Ranking ranking = rankingRepository.findByUsersAndQuestionAndGroups(user, question, group)
                .orElseGet(() -> createRanking(user, question, group));
        ranking.increaseCount();
    }
}
