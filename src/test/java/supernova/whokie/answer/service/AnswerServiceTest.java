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
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.repository.FriendRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AnswerServiceTest {
    @MockBean
    private AnswerRepository answerRepository;
    @MockBean
    private FriendRepository friendRepository;

    @Autowired
    private AnswerService answerService;


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
        when(friendRepository.findRandomFriendsByHostUser(dummyUser.getId(), AnswerService.FRIEND_LIMIT)).thenReturn(dummyFriends);

        AnswerResponse.Refresh refreshResponse = answerService.refreshAnswerList(dummyUser);

        //then
        assertEquals(5, refreshResponse.users().size());

        verify(friendRepository, times(1)).findRandomFriendsByHostUser(dummyUser.getId(), AnswerService.FRIEND_LIMIT);
    }
}