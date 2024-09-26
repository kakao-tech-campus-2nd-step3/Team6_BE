package supernova.whokie.question.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.question.Question;
import supernova.whokie.question.controller.dto.QuestionModel;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest
class QuestionServiceTest {

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private FriendRepository friendRepository;

    @MockBean
    private UserRepository userRepository;

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
}