package supernova.whokie.question.service;

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
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@TestPropertySource(properties = {
        "spring.profiles.active=default",
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

    @Test
    @DisplayName("질문과 친구 목록을 정상적으로 가져오는지 테스트")
    void getCommonQuestionTest() {
        // given
        Users dummyUser = Users.builder().id(1L).build();

        List<Question> dummyQuestions = List.of(
                Question.builder().id(1L).content("Question 1").build(),
                Question.builder().id(2L).content("Question 2").build(),
                Question.builder().id(3L).content("Question 3").build(),
                Question.builder().id(4L).content("Question 4").build(),
                Question.builder().id(5L).content("Question 5").build(),
                Question.builder().id(6L).content("Question 6").build(),
                Question.builder().id(7L).content("Question 7").build(),
                Question.builder().id(8L).content("Question 8").build(),
                Question.builder().id(9L).content("Question 9").build(),
                Question.builder().id(10L).content("Question 10").build()
        );

        List<Friend> dummyFriends = List.of(
                Friend.builder()
                        .hostUser(dummyUser)
                        .friendUser(Users.builder().id(2L).name("Friend 1").imageUrl("url1").build())
                        .build(),
                Friend.builder()
                        .hostUser(dummyUser)
                        .friendUser(Users.builder().id(3L).name("Friend 2").imageUrl("url2").build())
                        .build(),
                Friend.builder()
                        .hostUser(dummyUser)
                        .friendUser(Users.builder().id(4L).name("Friend 3").imageUrl("url3").build())
                        .build(),
                Friend.builder()
                        .hostUser(dummyUser)
                        .friendUser(Users.builder().id(5L).name("Friend 4").imageUrl("url4").build())
                        .build(),
                Friend.builder()
                        .hostUser(dummyUser)
                        .friendUser(Users.builder().id(6L).name("Friend 5").imageUrl("url5").build())
                        .build()
        );

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(dummyUser));
        when(questionRepository.findRandomQuestions(any(Pageable.class))).thenReturn(dummyQuestions);
        when(friendRepository.findRandomFriendsByHostUser(eq(dummyUser.getId()), any(Pageable.class))).thenReturn(dummyFriends);

        List<QuestionModel.CommonQuestion> commonQuestionList = questionService.getCommonQuestion(dummyUser.getId());
        QuestionResponse.CommonQuestions commonQuestions = QuestionResponse.CommonQuestions.from(commonQuestionList);


        // then
        assertEquals(10, commonQuestions.questions().size());
        assertEquals(5, commonQuestions.questions().get(0).users().size());

    }

    @Test
    @DisplayName("랜덤 그룹 질문 조회 테스트")
    void getGroupQuestionTest() {
        // given
        List<Question> dummyQuestions = List.of(
            Question.builder().id(1L).content("Question 1").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(2L).content("Question 2").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(3L).content("Question 3").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(4L).content("Question 4").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(5L).content("Question 5").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(6L).content("Question 6").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(7L).content("Question 7").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(8L).content("Question 8").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(9L).content("Question 9").questionStatus(QuestionStatus.APPROVED).groupId(1L).build(),
            Question.builder().id(10L).content("Question 10").questionStatus(QuestionStatus.APPROVED).groupId(1L).build()
        );

        List<GroupMember> dummyGroupMembers = List.of(
            GroupMember.builder().id(1L).user(Users.builder().id(1L).build()).build(),
            GroupMember.builder().id(2L).user(Users.builder().id(2L).build()).build(),
            GroupMember.builder().id(3L).user(Users.builder().id(3L).build()).build(),
            GroupMember.builder().id(4L).user(Users.builder().id(4L).build()).build(),
            GroupMember.builder().id(5L).user(Users.builder().id(5L).build()).build()
        );

        // when
        when(groupMemberRepository.findByUserIdAndGroupId(anyLong(), anyLong())).thenReturn(Optional.of(dummyGroupMembers.get(0)));
        when(questionRepository.findRandomGroupQuestions(anyLong(), any(Pageable.class))).thenReturn(dummyQuestions);
        when(groupMemberRepository.getRandomGroupMember(eq(1L), anyLong(), any(Pageable.class))).thenReturn(dummyGroupMembers);

        List<GroupQuestion> groupQuestionList = questionService.getGroupQuestions(1L, 1L);
        QuestionResponse.GroupQuestions groupQuestions = QuestionResponse.GroupQuestions.from(groupQuestionList);

        // then
        assertEquals(10, groupQuestions.questions().size());
        assertEquals(5, groupQuestions.questions().get(0).users().size());
    }

    @Test
    @DisplayName("그룹 질문 생성 테스트")
    void createQuestion() {
        // given
        Users user = Users.builder()
            .name("testUser")
            .build();

        GroupMember groupMember = GroupMember.builder()
            .user(user)
            .groupRole(GroupRole.MEMBER)
            .groupStatus(GroupStatus.APPROVED)
            .build();

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
    void approveQuestion() {
        // given
        Users user = Users.builder()
            .name("testUser")
            .build();

        GroupMember leader = GroupMember.builder()
            .user(user)
            .groupRole(GroupRole.LEADER)
            .groupStatus(GroupStatus.APPROVED)
            .build();

        Question question = Question.builder()
            .groupId(1L)
            .questionStatus(QuestionStatus.READY).build();

        QuestionCommand.Approve approveCommand = new QuestionCommand.Approve(1L, 1L, true);

        given(groupMemberRepository.findByUserIdAndGroupId(anyLong(), anyLong())).willReturn(Optional.of(leader));
        given(questionRepository.findByIdAndGroupId(anyLong(), anyLong())).willReturn(Optional.of(question));

        // when
        questionService.approveQuestion(1L, approveCommand);

        // then
        verify(questionRepository).findByIdAndGroupId(anyLong(), anyLong());
        assertThat(question.getQuestionStatus()).isEqualTo(QuestionStatus.APPROVED);
    }
}