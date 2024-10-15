package supernova.whokie.question.service;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.service.dto.QuestionCommand;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.question.service.dto.QuestionModel.GroupQuestion;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=abcd"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestionServiceTest {

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private FriendRepository friendRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private QuestionService questionService;

    private Users user;
    private List<Question> questions;

    @BeforeEach
    void setUp() {
        user = createUser();
    }

    @Test
    @DisplayName("질문과 친구 목록을 정상적으로 가져오는지 테스트")
    void getCommonQuestionTest() {
        // given
        questions = createQuestions(10);
        List<Friend> friends = createFriends(user, 5);

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(questionRepository.findRandomQuestions(any(Pageable.class))).thenReturn(questions);
        when(friendRepository.findRandomFriendsByHostUser(eq(user.getId()), any(Pageable.class))).thenReturn(friends);

        List<QuestionModel.CommonQuestion> commonQuestionList = questionService.getCommonQuestion(user.getId());
        QuestionResponse.CommonQuestions commonQuestions = QuestionResponse.CommonQuestions.from(commonQuestionList);


        // then
        assertAll(
            () -> assertEquals(10, commonQuestions.questions().size()),
            () -> assertEquals(5, commonQuestions.questions().get(0).users().size())
        );
    }

    @Test
    @DisplayName("랜덤 그룹 질문 조회 테스트")
    void getGroupQuestionTest() {
        // given
        questions = createQuestions(10);
        List<GroupMember> groupMembers = createGroupMembers(5);

        // when
        when(groupMemberRepository.findByUserIdAndGroupId(anyLong(), anyLong())).thenReturn(Optional.of(groupMembers.get(0)));
        when(questionRepository.findRandomGroupQuestions(anyLong(), any(Pageable.class))).thenReturn(questions);
        when(groupMemberRepository.getRandomGroupMember(eq(1L), anyLong(), any(Pageable.class))).thenReturn(groupMembers);

        List<GroupQuestion> groupQuestionList = questionService.getGroupQuestions(1L, 1L);
        QuestionResponse.GroupQuestions groupQuestions = QuestionResponse.GroupQuestions.from(groupQuestionList);

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
        GroupMember groupMember = createGroupMember(GroupRole.MEMBER);

        QuestionCommand.Create command = new QuestionCommand.Create(1L, "Test question");

        given(groupMemberRepository.findByUserIdAndGroupId(any(Long.class), any(Long.class)))
            .willReturn(Optional.of(groupMember));

        // when
        questionService.createQuestion(1L, command);

        // then
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("그룹 질문 승인 테스트")
    void approveQuestionTest() {
        // given
        GroupMember leader = createGroupMember(GroupRole.LEADER);

        Question question = createQuestion();

        QuestionCommand.Approve approveCommand = new QuestionCommand.Approve(1L, question.getId(),
            true);

        given(groupMemberRepository.findByUserIdAndGroupId(anyLong(), anyLong())).willReturn(
            Optional.of(leader));
        given(questionRepository.findByIdAndGroupId(anyLong(), anyLong())).willReturn(
            Optional.of(question));

        // when
        questionService.approveQuestion(1L, approveCommand);

        // then
        assertAll(
            () -> verify(questionRepository).findByIdAndGroupId(anyLong(), anyLong()),
            () -> assertThat(question.getQuestionStatus()).isEqualTo(QuestionStatus.APPROVED)
        );
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