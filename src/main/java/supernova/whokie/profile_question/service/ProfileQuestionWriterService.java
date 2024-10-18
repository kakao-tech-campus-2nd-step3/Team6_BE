package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.infrastructure.repository.ProfileQuestionRepository;

@Service
@RequiredArgsConstructor
public class ProfileQuestionWriterService {

    private final ProfileQuestionRepository profileQuestionRepository;

    @Transactional
    public void save(ProfileQuestion profileQuestion) {
        profileQuestionRepository.save(profileQuestion);
    }

    @Transactional
    public void deleteById(Long id) {
        profileQuestionRepository.deleteById(id);
    }
}
