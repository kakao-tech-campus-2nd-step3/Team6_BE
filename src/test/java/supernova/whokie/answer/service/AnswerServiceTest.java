package supernova.whokie.answer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AnswerServiceTest {
    @MockBean
    private AnswerRepository answerRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private UsersRepository usersRepository;

    @Autowired
    private AnswerService answerService;


    @Test
    @DisplayName("전체 질문 기록을 가져오는 메서드 테스트")
    void getAnswerRecordTest() {
        // given
        Users dummyUser = mock(Users.class);
        Answer dummyAnswer = Answer.builder()
                .id(1L)
                .question(mock(Question.class))
                .picker(mock(Users.class))
                .picked(mock(Users.class))
                .hintCount(3)
                .build();
        ReflectionTestUtils.setField(dummyAnswer, "createdAt", LocalDateTime.of(2024, 9, 19, 0,0));

        Page<Answer> answerPage = new PageImpl<>(List.of(dummyAnswer), PageRequest.of(0, 10), 1);

        // when
        when(answerRepository.findAllByPicker(any(Pageable.class), eq(dummyUser))).thenReturn(answerPage);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());

        PagingResponse<AnswerResponse.Record> response = answerService.getAnswerRecord(pageable, dummyUser);

        // then
        assertEquals(1, response.content().size());
        assertEquals(dummyAnswer.getId(), response.content().get(0).answerId());
        assertEquals(3, response.content().get(0).hintCount());
    }

    @Test
    @DisplayName("공통 질문 답하기 메서드의 save가 잘 작동하는지 테스트")
    void answerToCommonQuestionSaveTest() {
        // given
        Users user = mock(Users.class);
        Question question = mock(Question.class);
        Users picked = mock(Users.class);

        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(picked));

        // when
        answerService.answerToCommonQuestion(user, 1L, 2L);

        // then
        verify(answerRepository, times(1)).save(any(Answer.class));
    }
}