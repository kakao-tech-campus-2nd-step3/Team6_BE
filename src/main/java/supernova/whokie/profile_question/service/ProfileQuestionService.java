package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.global.exception.EntityNotFoundException;
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

    public void deleteProfileQuestion(Long profileQuestionId) {
        profileQuestionRepository.findById(profileQuestionId)
            .orElseThrow(() -> new EntityNotFoundException("해당하는 프로필 질문이 존재하지 않습니다."));

        profileQuestionRepository.deleteById(profileQuestionId);
    }
}
