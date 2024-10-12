package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.profile_answer.infrastructure.repository.ProfileAnswerRepository;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.infrastructure.repository.ProfileQuestionRepository;
import supernova.whokie.profile_question.service.dto.ProfileQuestionCommand.Create;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileQuestionService {

    private final ProfileQuestionRepository profileQuestionRepository;
    private final ProfileAnswerRepository profileAnswerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProfileQuestionModel.Info> getProfileQuestions(Long userId, Pageable pageable) {
        Page<ProfileQuestion> profileQuestions = profileQuestionRepository.findAllByUserId(userId,
            pageable);
        return profileQuestions.map(ProfileQuestionModel.Info::from);
    }

    @Transactional
    public void deleteProfileQuestion(Long userId, Long profileQuestionId) {
        ProfileQuestion profileQuestion = profileQuestionRepository.findByIdWithUser(
                profileQuestionId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.PROFILE_QUESTION_NOT_FOUND_MESSAGE));

        if (!profileQuestion.isOwner(userId)) {
            throw new ForbiddenException("질문을 작성한 사람만 삭제할 수 있습니다.");
        }

        profileAnswerRepository.deleteAllByProfileQuestionId(profileQuestionId);
        profileQuestionRepository.deleteById(profileQuestionId);
    }

    @Transactional
    public void createProfileQuestion(Long userId, Create command) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        ProfileQuestion profileQuestion = command.toEntity(user);
        
        profileQuestionRepository.save(profileQuestion);
    }
}
