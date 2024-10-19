package supernova.whokie.question.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.group_member.service.GroupMemberReaderService;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestionServiceTest {

    @Autowired
    private QuestionService questionService;

    @MockBean
    private GroupMemberReaderService groupMemberReaderService;

    @MockBean
    private FriendReaderService friendReaderService;

    @MockBean
    private QuestionReaderService questionReaderService;

    @MockBean
    private UserReaderService userReaderService;

    @MockBean
    private QuestionWriterService questionWriterService;

    private Users user;
    private List<Question> questions;
    private List<GroupMember> groupMembers;
    private List<Friend> friends;
    private GroupMember groupMember;
    private GroupMember leaderGroupMember;
    private Question question;


    @BeforeEach
    void setUp() {
        user = createUser();
        questions = createQuestions(10);
        groupMembers = createGroupMembers(5);
        friends = createFriends(user,5);
        groupMember = createGroupMember(GroupRole.MEMBER);
        leaderGroupMember = createGroupMember(GroupRole.LEADER);
        question = createQuestion();
    }

    @Test
    @DisplayName("랜덤 질문과 친구 목록을 정상적으로 가져오는지 테스트")
    void getCommonQuestionTest() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, Constants.QUESTION_LIMIT);
        Pageable friendPageable = PageRequest.of(0, Constants.FRIEND_LIMIT);

        // when
        when(userReaderService.getUserById(eq(userId))).thenReturn(user);
        when(questionReaderService.getRandomQuestions(eq(pageable))).thenReturn(questions);
        when(friendReaderService.findRandomFriendsByHostUser(eq(userId), eq(friendPageable)))
                .thenReturn(friends);

        List<QuestionModel.CommonQuestion> commonQuestions = questionService.getCommonQuestion(userId, pageable);

        // then
        assertAll(
                () -> assertEquals(10, commonQuestions.size()),
                () -> assertEquals(5, commonQuestions.get(0).users().size())
        );
    }

    @Test
    @DisplayName("랜덤 그룹 질문 조회 테스트")
    void getGroupQuestionTest() {
        // given
        Long userId = 1L;
        Long groupId = 1L;

        // when
        when(groupMemberReaderService.isGroupMemberExist(eq(userId), eq(groupId)))
                .thenReturn(true);
        when(questionReaderService.getRandomGroupQuestions(eq(groupId), any(Pageable.class)))
                .thenReturn(questions);
        when(groupMemberReaderService.getRandomGroupMembersByGroupId(eq(userId), eq(groupId), any(Pageable.class)))
                .thenReturn(groupMembers);

        Pageable pageable = PageRequest.of(0, Constants.QUESTION_LIMIT);
        List<QuestionModel.GroupQuestion> groupQuestionList = questionService.getGroupQuestions(
                userId, groupId);
        QuestionResponse.GroupQuestions groupQuestions = QuestionResponse.GroupQuestions.from(
                groupQuestionList);

        // then
        assertAll(
                () -> assertEquals(10, groupQuestions.questions().size()),
                () -> assertEquals(5, groupQuestions.questions().get(0).users().size())
        );
    }

    @Test
    @DisplayName("그룹 질문 생성 테스트")
    void createQuestionTest() {
        // given
        QuestionCommand.Create command = new QuestionCommand.Create(1L, "Test Question");

        // when
        when(groupMemberReaderService.getByUserIdAndGroupId(anyLong(), anyLong()))
                .thenReturn(groupMember);

        questionService.createQuestion(user.getId(), command);

        // then
        verify(groupMemberReaderService, times(1)).getByUserIdAndGroupId(anyLong(), anyLong());
        verify(questionWriterService, times(1)).save(any(Question.class));
    }

    @Test //TODO 수정
    @DisplayName("그룹 질문 승인 테스트")
    void approveQuestionTest() {
        // given
        QuestionCommand.Approve command = new QuestionCommand.Approve(1L, 1L, true);

        // when
        when(groupMemberReaderService.getByUserIdAndGroupId(anyLong(), anyLong()))
                .thenReturn(leaderGroupMember);
        when(questionReaderService.getQuestionByIdAndGroupId(anyLong(), anyLong()))
                .thenReturn(question);


        questionService.approveQuestion(user.getId(), command);

        // then
        verify(groupMemberReaderService, times(1)).getByUserIdAndGroupId(anyLong(), anyLong());
        verify(questionReaderService, times(1)).getQuestionByIdAndGroupId(anyLong(), anyLong());
        assertEquals(QuestionStatus.APPROVED, question.getQuestionStatus());
    }

    private Users createUser() {
        return Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(1500)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();
    }

    private List<Question> createQuestions(int count) {
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            questions.add(Question.builder()
                .id((long) i)
                .content("Question " + i)
                .build());
        }
        return questions;
    }

    private List<Friend> createFriends(Users user, int count) {
        List<Friend> friends = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            friends.add(Friend.builder()
                .hostUser(user)
                .friendUser(Users.builder()
                    .id((long) (i + 1))
                    .name("Friend " + i)
                    .imageUrl("url" + i)
                    .build())
                .build());
        }
        return friends;
    }

    private List<GroupMember> createGroupMembers(int count) {
        List<GroupMember> groupMembers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            groupMembers.add(GroupMember.builder()
                .id((long) i)
                .user(Users.builder()
                    .id((long) i)
                    .build())
                .build());
        }
        return groupMembers;
    }

    private GroupMember createGroupMember(GroupRole groupRole) {
        return GroupMember.builder()
            .user(user)
            .groupRole(groupRole)
            .groupStatus(GroupStatus.APPROVED)
            .build();
    }

    private Question createQuestion() {
        return Question.builder()
            .id(1L)
            .groupId(1L)
            .questionStatus(QuestionStatus.READY).build();
    }
}