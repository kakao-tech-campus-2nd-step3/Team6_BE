package supernova.whokie.question.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.GroupRole;
import supernova.whokie.groupmember.GroupStatus;
import supernova.whokie.groupmember.service.GroupMemberReaderService;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.constants.QuestionConstants;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private GroupMemberReaderService groupMemberReaderService;

    @Mock
    private FriendReaderService friendReaderService;

    @Mock
    private QuestionReaderService questionReaderService;

    @Mock
    private UserReaderService userReaderService;

    @Mock
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
        questions = createGroupQuestions(10);
        groupMembers = createGroupMembers(5);
        friends = createFriends(user,5);
        groupMember = createGroupMember(GroupRole.MEMBER);
        leaderGroupMember = createGroupMember(GroupRole.LEADER);
        question = createGroupQuestion();
    }

    @Test
    @DisplayName("랜덤 질문과 친구 목록을 정상적으로 가져오는지 테스트")
    void getCommonQuestionTest() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, QuestionConstants.QUESTION_LIMIT);

        // when
        when(questionReaderService.getRandomQuestions(eq(pageable))).thenReturn(questions);

        List<QuestionModel.CommonQuestion> commonQuestions = questionService.getCommonQuestion(pageable);

        // then
        assertAll(
                () -> assertEquals(10, commonQuestions.size())
        );
    }

    @Test
    @DisplayName("그룹 질문 생성 테스트")
    void createGroupQuestionTest() {
        // given
        QuestionCommand.Create command = new QuestionCommand.Create(1L, "Test Question");

        // when
        when(groupMemberReaderService.getByUserIdAndGroupId(anyLong(), anyLong()))
                .thenReturn(groupMember);

        questionService.createGroupQuestion(user.getId(), command);

        // then
        verify(groupMemberReaderService, times(1)).getByUserIdAndGroupId(anyLong(), anyLong());
        verify(questionWriterService, times(1)).save(any(Question.class));
    }

    @Test
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
            .birthDate(LocalDate.now())
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .imageUrl("url")
            .build();
    }

    private List<Question> createGroupQuestions(int count) {
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
                    .imageUrl("url" + i)
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

    private Question createGroupQuestion() {
        return Question.builder()
            .id(1L)
            .groupId(1L)
            .questionStatus(QuestionStatus.READY).build();
    }
}