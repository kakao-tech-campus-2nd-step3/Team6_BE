package supernova.whokie.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
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
    public void increaseRankingCountByUserAndQuestion(Users user, String question) {
        Ranking ranking = rankingRepository.findByUsersAndQuestion(user, question)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
        ranking.increaseCount();
    }
}
