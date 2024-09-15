package supernova.whokie.answer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceTest {
    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("전체 질문 기록을 가져오는 메서드 테스트")
    void getAnswerRecordTest() {
        Users dummyUser = mock(Users.class);
        // given
        Answer dummyAnswer = Answer.builder()
                .id(1L)
                .question(mock(Question.class))
                .picker(mock(Users.class))
                .picked(mock(Users.class))
                .hintCount(3)
                .build();

        dummyAnswer.configCreatedAt(LocalDateTime.now());

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
}