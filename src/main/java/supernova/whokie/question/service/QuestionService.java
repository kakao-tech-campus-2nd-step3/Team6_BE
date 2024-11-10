package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.service.GroupMemberReaderService;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final GroupMemberReaderService groupMemberReaderService;
    private final QuestionReaderService questionReaderService;
    private final QuestionWriterService questionWriterService;
    private final UserReaderService userReaderService;

    @Transactional(readOnly = true)
    public List<QuestionModel.CommonQuestion> getCommonQuestion(Pageable pageable) {
        List<Question> randomQuestions = questionReaderService.getRandomQuestions(pageable);

        return randomQuestions.stream()
            .map(QuestionModel.CommonQuestion::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<QuestionModel.Info> getGroupQuestionPaging(Long userId, Long groupId,
        QuestionStatus status, Pageable pageable) {

        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }

        Page<Question> groupQuestionPage = questionReaderService.getAllByStatus(groupId, status,
            pageable);

        return groupQuestionPage.map(question -> QuestionModel.Info.from(question, status));
    }

    @Transactional(readOnly = true)
    public List<QuestionModel.GroupQuestion> getGroupQuestions(Long userId, Long groupId, Pageable pageable) {

        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }

        List<Question> randomQuestions = questionReaderService.getRandomGroupQuestions(groupId, pageable);

        return randomQuestions.stream()
            .map(QuestionModel.GroupQuestion::from)
            .toList();
    }

    @Transactional
    public void createGroupQuestion(Long userId, QuestionCommand.Create command) {
        GroupMember groupMember = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());
        groupMember.validateApprovalStatus();

        Question question = command.toEntity(groupMember.getUser());

        questionWriterService.save(question);
    }

    @Transactional
    public void createCommonQuestion(Long userId, QuestionCommand.Create command) {
        Users admin = userReaderService.getUserById(userId);
        Question question = command.toEntity(admin);

        questionWriterService.save(question);
    }

    @Transactional
    public void deleteCommonQuestion(Long userId, Long questionId) {
        Users admin = userReaderService.getUserById(userId);
        questionWriterService.delete(questionId);
    }

    @Transactional
    public void approveQuestion(Long userId, QuestionCommand.Approve command) {
        GroupMember groupMember = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());
        groupMember.validateLeaderApprovalAuthority();

        Question question = questionReaderService.getQuestionByIdAndGroupId(command.questionId(),
            command.groupId());

        question.changeStatus(command.status());
    }

    @Transactional(readOnly = true)
    public Page<QuestionModel.Admin> getAllQuestionPaging(Pageable pageable) {
        Page<Question> entities = questionReaderService.getAllQuestionPaging(pageable);
        return entities.map(QuestionModel.Admin::from);
    }
}
