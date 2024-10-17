package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.profile_answer.service.ProfileAnswerWriterService;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.service.dto.ProfileQuestionCommand.Create;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class ProfileQuestionService {

    private final ProfileQuestionReaderService profileQuestionReaderService;
    private final ProfileAnswerWriterService profileAnswerWriterService;
    private final UserReaderService userReaderService;
    private final ProfileQuestionWriterService profileQuestionWriterService;

    @Transactional(readOnly = true)
    public Page<ProfileQuestionModel.Info> getProfileQuestions(Long userId, Pageable pageable) {
        Page<ProfileQuestion> profileQuestions = profileQuestionReaderService.getAllByUserId(userId,
            pageable);
        return profileQuestions.map(ProfileQuestionModel.Info::from);
    }

    @Transactional
    public void deleteProfileQuestion(Long userId, Long profileQuestionId) {
        ProfileQuestion profileQuestion = profileQuestionReaderService.getByIdWithUser(
            profileQuestionId);
        if (!profileQuestion.isOwner(userId)) {
            throw new ForbiddenException("질문을 작성한 사람만 삭제할 수 있습니다.");
        }

        profileAnswerWriterService.deleteAllByProfileQuestionId(profileQuestionId);
        profileQuestionWriterService.deleteById(profileQuestionId);
    }

    @Transactional
    public void createProfileQuestion(Long userId, Create command) {
        Users user = userReaderService.getUserById(userId);
        ProfileQuestion profileQuestion = command.toEntity(user);

        profileQuestionWriterService.save(profileQuestion);
    }
}
