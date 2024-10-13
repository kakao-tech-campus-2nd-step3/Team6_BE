package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberModel;
import supernova.whokie.group_member.service.dto.GroupMemberModel.Option;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {


    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public List<QuestionModel.CommonQuestion> getCommonQuestion(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        return getCommonQuestionList(user);
    }

    @Transactional(readOnly = true)
    public Page<QuestionModel.Info> getGroupQuestionPaging(Long userId, String groupId, QuestionStatus status, Pageable pageable) {
        Long parsedGroupId = Long.parseLong(groupId);

        groupMemberRepository.findByUserIdAndGroupId(userId, parsedGroupId).orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        Page<Question> groupQuestionPage = questionRepository.findAllByStatus(parsedGroupId, status, pageable);

        return groupQuestionPage.map(question -> QuestionModel.Info.from(question, status));
    }


    private List<QuestionModel.CommonQuestion> getCommonQuestionList(Users user) {
        Pageable pageable = PageRequest.of(0, Constants.QUESTION_LIMIT);

        List<Question> randomQuestions = questionRepository.findRandomQuestions(pageable);

        return randomQuestions.stream()
                .map(question -> QuestionModel.CommonQuestion.from(question, getFriendList(user)))
                .toList();
    }

    private List<UserModel.PickedInfo> getFriendList(Users user) {
        Pageable pageable = PageRequest.of(0, Constants.FRIEND_LIMIT);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        return randomFriends.stream()
                .map(friend -> UserModel.PickedInfo.from(friend.getFriendUser()))
                .toList();
    }

    public List<QuestionModel.GroupQuestion> getGroupQuestions(Long userId, Long groupId) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        Pageable pageable = PageRequest.of(0, Constants.QUESTION_LIMIT);
        List<Question> randomQuestions = questionRepository.findRandomGroupQuestions(groupId, pageable);

        return randomQuestions.stream()
                .map(question -> QuestionModel.GroupQuestion.from(question, getGroupMemberList(userId, groupId)))
                .toList();
    }

    private List<GroupMemberModel.Option> getGroupMemberList(Long userId, Long groupId) {
        Pageable pageable = PageRequest.of(0, Constants.FRIEND_LIMIT);
        List<GroupMember> randomGroupMembers = groupMemberRepository.getRandomGroupMember(userId, groupId, pageable);

        return randomGroupMembers.stream()
                .map(Option::from)
                .toList();
    }

    @Transactional
    public void createQuestion(Long userId, QuestionCommand.Create command) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
        validateApprovalStatus(groupMember);

        Question question = command.toEntity(groupMember.getUser());

        questionRepository.save(question);
    }

    public void validateApprovalStatus(GroupMember groupMember) {
        if (!groupMember.isApproved()) {
            throw new IllegalStateException(MessageConstants.NOT_APPROVED_MEMBER_MESSAGE);
        }
    }

    @Transactional
    public void approveQuestion(Long userId, QuestionCommand.Approve command) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
        groupMember.validateLeader();

        Question question = questionRepository.findByIdAndGroupId(command.questionId(),
                        command.groupId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_QUESTION_NOT_FOUND_MESSAGE));

        question.changeStatus(command.status());
    }
    private static QuestionStatus getQuestionStatusByRequestStatus(Boolean status) {
        return status ? QuestionStatus.APPROVED : QuestionStatus.REJECTED;
    }
}
