package supernova.whokie.profile_answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.profile_answer.ProfileAnswer;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerCommand;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerModel;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.service.ProfileQuestionReaderService;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class ProfileAnswerService {

    private final ProfileAnswerReaderService profileAnswerReaderService;
    private final ProfileAnswerWriterService profileAnswerWriterService;
    private final UserReaderService userReaderService;
    private final ProfileQuestionReaderService profileQuestionReaderService;


    @Transactional(readOnly = true)
    public Page<ProfileAnswerModel.Info> getProfileAnswers(Long userId, Pageable pageable) {
        Page<ProfileAnswer> profileAnswers = profileAnswerReaderService.getAllByUserId(userId,
            pageable);
        return profileAnswers.map(ProfileAnswerModel.Info::from);
    }

    @Transactional
    public void createProfileAnswer(Long answeredUserId, ProfileAnswerCommand.Create command) {
        Users answeredUser = userReaderService.getUserById(answeredUserId);
        ProfileQuestion profileQuestion = profileQuestionReaderService.getById(
            command.profileQuestionId());
        ProfileAnswer profileAnswer = command.toEntity(answeredUser, profileQuestion);

        profileAnswerWriterService.save(profileAnswer);
    }

    @Transactional
    public void deleteProfileAnswer(Long userId, Long profileAnswerId) {
        ProfileAnswer profileAnswer = profileAnswerReaderService.getByIdWithAnsweredUser(
            profileAnswerId);
        if (!profileAnswer.isOwner(userId)) {
            throw new ForbiddenException("답변을 작성한 사람만 삭제할 수 있습니다.");
        }
        profileAnswerWriterService.deleteById(profileAnswerId);
    }
}
