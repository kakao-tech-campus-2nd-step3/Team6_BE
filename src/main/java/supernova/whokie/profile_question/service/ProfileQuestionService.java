package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.infrastructure.repository.ProfileQuestionRepository;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

@Service
@RequiredArgsConstructor
public class ProfileQuestionService {

    private final ProfileQuestionRepository profileQuestionRepository;

    public Page<ProfileQuestionModel.Info> getProfileQuestions(Long userId, Pageable pageable) {
        Page<ProfileQuestion> profileQuestions = profileQuestionRepository.findAllByUserId(userId,
            pageable);
        return profileQuestions.map(ProfileQuestionModel.Info::from);
    }
}
