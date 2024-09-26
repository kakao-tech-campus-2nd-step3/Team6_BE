package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.profile_answer.infrastructure.repository.ProfileAnswerRepository;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.infrastructure.repository.ProfileQuestionRepository;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

@Service
@RequiredArgsConstructor
public class ProfileQuestionService {

    private final ProfileQuestionRepository profileQuestionRepository;
    private final ProfileAnswerRepository profileAnswerRepository;


    public Page<ProfileQuestionModel.Info> getProfileQuestions(Long userId, Pageable pageable) {
        Page<ProfileQuestion> profileQuestions = profileQuestionRepository.findAllByUserId(userId,
            pageable);
        return profileQuestions.map(ProfileQuestionModel.Info::from);
    }

    public void deleteProfileQuestion(Long userId, Long profileQuestionId) {
        ProfileQuestion profileQuestion = profileQuestionRepository.findByIdWithUser(
                profileQuestionId)
            .orElseThrow(() -> new EntityNotFoundException("해당하는 프로필 질문이 존재하지 않습니다."));

        if (!profileQuestion.isOwner(userId)) {
            throw new ForbiddenException("질문을 작성한 사람만 삭제할 수 있습니다.");
        }

        profileAnswerRepository.deleteAllByProfileQuestionId(profileQuestionId);
        profileQuestionRepository.deleteById(profileQuestionId);
    }
}
