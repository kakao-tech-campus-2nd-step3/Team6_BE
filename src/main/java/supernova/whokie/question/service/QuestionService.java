package supernova.whokie.question.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.service.GroupMemberReaderService;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final GroupMemberReaderService groupMemberReaderService;
    private final QuestionReaderService questionReaderService;
    private final FriendReaderService friendReaderService;
    private final UserReaderService userReaderService;
    private final QuestionWriterService questionWriterService;

    @Transactional(readOnly = true)
    public List<QuestionModel.CommonQuestion> getCommonQuestion(Long userId, Pageable pageable) {

        Users user = userReaderService.getUserById(userId);

        List<Question> randomQuestions = questionReaderService.getRandomQuestions(pageable);

        return randomQuestions.stream()
            .map(question -> QuestionModel.CommonQuestion.from(question, getFriendList(user)))
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

    private List<UserModel.PickedInfo> getFriendList(Users user) {
        Pageable pageable = PageRequest.of(0, Constants.FRIEND_LIMIT);
        List<Friend> randomFriends = friendReaderService.findRandomFriendsByHostUser(user.getId(),
            pageable);

        return randomFriends.stream()
            .map(friend -> UserModel.PickedInfo.from(friend.getFriendUser()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionModel.GroupQuestion> getGroupQuestions(Long userId, Long groupId) {

        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }

        Pageable pageable = PageRequest.of(0, Constants.QUESTION_LIMIT);
        List<Question> randomQuestions = questionReaderService.getRandomGroupQuestions(groupId,
            pageable);

        Pageable GroupMemberpageable = PageRequest.of(0, Constants.FRIEND_LIMIT);

        //TODO 리팩토링 필요
        return randomQuestions.stream()
            .map(question -> QuestionModel.GroupQuestion.from(question,
                groupMemberReaderService.getRandomGroupMembersByGroupId(userId, groupId,
                    GroupMemberpageable)))
            .toList();
    }

    @Transactional
    public void createQuestion(Long userId, QuestionCommand.Create command) {
        GroupMember groupMember = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());
        groupMember.validateApprovalStatus();

        Question question = command.toEntity(groupMember.getUser());

        questionWriterService.save(question);
    }

    @Transactional
    public void approveQuestion(Long userId, QuestionCommand.Approve command) {
        GroupMember groupMember = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());
        groupMember.validateLeader();

        Question question = questionReaderService.getQuestionByIdAndGroupId(command.questionId(),
            command.groupId());

        question.changeStatus(command.status());
    }
}
