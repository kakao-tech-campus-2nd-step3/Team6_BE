package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberModel;
import supernova.whokie.group_member.service.dto.GroupMemberModel.Option;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

import java.util.List;
import supernova.whokie.user.service.dto.UserModel.PickedInfo;

@Service
@RequiredArgsConstructor
public class QuestionService {

    @Value("${friend-limit}")
    private int friendLimit;

    @Value("${question-limit}")
    private int questionLimit;

    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public List<QuestionModel.CommonQuestion> getCommonQuestion(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        return getCommonQuestionList(user);
    }


    private List<QuestionModel.CommonQuestion> getCommonQuestionList(Users user) {
        Pageable pageable = PageRequest.of(0, questionLimit);

        List<Question> randomQuestions = questionRepository.findRandomQuestions(pageable);

        return randomQuestions.stream()
                .map(question -> QuestionModel.CommonQuestion.from(question, getFriendList(user)))
                .toList();
    }

    private List<UserModel.PickedInfo> getFriendList(Users user) {
        Pageable pageable = PageRequest.of(0, friendLimit);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        return randomFriends.stream()
                .map(friend -> UserModel.PickedInfo.from(friend.getFriendUser()))
                .toList();
    }

    public List<QuestionModel.GroupQuestion> getGroupQuestions(Long userId, Long groupId) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));

        Pageable pageable = PageRequest.of(0, questionLimit);
        List<Question> randomQuestions = questionRepository.findRandomGroupQuestions(groupId, pageable);

        return randomQuestions.stream()
            .map(question -> QuestionModel.GroupQuestion.from(question, getGroupMemberList(userId, groupId)))
            .toList();
    }

    private List<GroupMemberModel.Option> getGroupMemberList(Long userId, Long groupId) {
        Pageable pageable = PageRequest.of(0, friendLimit);
        List<GroupMember> randomGroupMembers = groupMemberRepository.getRandomGroupMember(userId, groupId, pageable);

        return randomGroupMembers.stream()
            .map(Option::from)
            .toList();
    }

    @Transactional
    public void createQuestion(Long userId, QuestionCommand.Create command) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        validateApprovalStatus(groupMember);

        Question question = command.toEntity(groupMember.getUser());

        questionRepository.save(question);
    }

    public void validateApprovalStatus(GroupMember groupMember) {
        if (!groupMember.isApproved()) {
            throw new IllegalStateException("승인되지 않은 멤버입니다.");
        }
    }

    @Transactional
    public void approveQuestion(Long userId, QuestionCommand.Approve command) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        groupMember.validateLeader();

        Question question = questionRepository.findByQuestionIdAndGroupId(command.questionId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 질문이 존재하지 않습니다."));

        question.changeStatus(command.status());
    }
}
