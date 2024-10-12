package supernova.whokie.profile_answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.profile_answer.ProfileAnswer;
import supernova.whokie.profile_answer.infrastructure.repository.ProfileAnswerRepository;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerCommand;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerModel;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.infrastructure.repository.ProfileQuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class ProfileAnswerService {

    private final UsersRepository usersRepository;
    private final ProfileQuestionRepository profileQuestionRepository;
    private final ProfileAnswerRepository profileAnswerRepository;

    public Page<ProfileAnswerModel.Info> getProfileAnswers(Long userId, Pageable pageable) {
        Page<ProfileAnswer> profileAnswers = profileAnswerRepository.findAllByUserId(userId,
            pageable);
        return profileAnswers.map(ProfileAnswerModel.Info::from);
    }

    public void createProfileAnswer(Long answeredUserId, ProfileAnswerCommand.Create command) {
        Users answeredUser = usersRepository.findById(answeredUserId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        ProfileQuestion profileQuestion = profileQuestionRepository.findById(
                command.profileQuestionId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.PROFILE_QUESTION_NOT_FOUND_MESSAGE));

        ProfileAnswer profileAnswer = command.toEntity(answeredUser, profileQuestion);

        profileAnswerRepository.save(profileAnswer);
    }

    public void deleteProfileAnswer(Long userId, Long profileAnswerId) {
        ProfileAnswer profileAnswer = profileAnswerRepository.findByIdWithAnsweredUser(
                profileAnswerId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.PROFILE_ANSWER_NOT_FOUND_MESSAGE));

        if (!profileAnswer.isOwner(userId)) {
            throw new ForbiddenException("답변을 작성한 사람만 삭제할 수 있습니다.");
        }
        profileAnswerRepository.deleteById(profileAnswerId);
    }
}
