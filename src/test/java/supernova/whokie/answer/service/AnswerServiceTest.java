package supernova.whokie.answer.service;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.answer.service.dto.AnswerCommand;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.profiles.active=default",
        "jwt.secret=abcd"
})
class AnswerServiceTest {

    @MockBean
    private AnswerRepository answerRepository;
    @MockBean
    private FriendRepository friendRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerService answerService;

    //@Test
//    @DisplayName("전체 질문 기록을 가져오는 메서드 테스트")
//    void getAnswerRecordTest() {
//        Users dummyUser = mock(Users.class);
//        // given
//        Answer dummyAnswer = Answer.builder()
//                .id(1L)
//                .question(mock(Question.class))
//                .picker(mock(Users.class))
//                .picked(mock(Users.class))
//                .hintCount(3)
//                .build();
//        ReflectionTestUtils.setField(dummyAnswer, "createdAt", LocalDateTime.of(2024, 9, 19, 0, 0));
//
//        Page<Answer> answerPage = new PageImpl<>(List.of(dummyAnswer), PageRequest.of(0, 10), 1);
//
//        // when
//        when(answerRepository.findAllByPicker(any(Pageable.class), eq(dummyUser))).thenReturn(
//                answerPage);
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(dummyUser));
//
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());
//
//        PagingResponse<AnswerResponse.Record> response = answerService.getAnswerRecord(pageable,
//                dummyUser.getId());
//
//        // then
//        assertEquals(1, response.content().size());
//        assertEquals(dummyAnswer.getId(), response.content().get(0).answerId());
//        assertEquals(3, response.content().get(0).hintCount());
//    }

    //@Test
    @DisplayName("공통 질문 답하기 메서드의 save가 잘 작동하는지 테스트")
    void answerToCommonQuestionSaveTest() {
        // given
        Long userId = 1L;
        Long questionId = 1L;
        Long pickedId = 2L;

        Users user = mock(Users.class);
        Question question = mock(Question.class);
        Users picked = mock(Users.class);

        AnswerCommand.CommonAnswer command = mock(AnswerCommand.CommonAnswer.class);
        Answer answer = mock(Answer.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findById(pickedId)).thenReturn(Optional.of(picked));

        when(command.questionId()).thenReturn(questionId);
        when(command.pickedId()).thenReturn(pickedId);

        when(command.toEntity(eq(question), eq(user), eq(picked), anyInt())).thenReturn(answer);

        // when
        answerService.answerToCommonQuestion(userId, command);

        // then
        verify(answerRepository, times(1)).save(answer);
    }

    //@Test
    @DisplayName("답변 새로고침 기능이 잘 동작하는지 확인하는 테스트")
    void refreshAnswerListTest() {
        //given
        Users dummyUser = Users.builder().id(1L).build();

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

        //when
        when(friendRepository.findAllByHostUser(any(Users.class))).thenReturn(dummyFriends);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(dummyUser));

        AnswerModel.Refresh refreshResponse = answerService.refreshAnswerList(dummyUser.getId());

        //then
        assertEquals(5, refreshResponse.users().size());

        verify(friendRepository, times(1)).findAllByHostUser(any(Users.class));
    }
}