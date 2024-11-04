package supernova.whokie.profilequestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profilequestion.ProfileQuestion;
import supernova.whokie.profilequestion.infrastructure.repository.ProfileQuestionRepository;

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
