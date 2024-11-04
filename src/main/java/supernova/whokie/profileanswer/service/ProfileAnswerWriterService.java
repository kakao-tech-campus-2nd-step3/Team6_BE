package supernova.whokie.profileanswer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profileanswer.ProfileAnswer;
import supernova.whokie.profileanswer.infrastructure.repository.ProfileAnswerRepository;

@Service
@RequiredArgsConstructor
public class ProfileAnswerWriterService {

    private final ProfileAnswerRepository profileAnswerRepository;

    @Transactional
    public void save(ProfileAnswer profileAnswer) {
        profileAnswerRepository.save(profileAnswer);
    }

    @Transactional
    public void deleteById(Long id) {
        profileAnswerRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByProfileQuestionId(Long profileQuestionId) {
        profileAnswerRepository.deleteAllByProfileQuestionId(profileQuestionId);
    }

}
