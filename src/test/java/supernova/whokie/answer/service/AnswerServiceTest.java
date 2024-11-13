package supernova.whokie.answer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.service.dto.AnswerCommand;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.pointrecord.event.PointRecordEventDto;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerReaderService answerReaderService;

    @Mock
    private UserReaderService userReaderService;

    @Mock
    private FriendReaderService friendReaderService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AnswerService answerService;

    private Users user;
    private Answer answer;
    private List<Friend> friends;

    @BeforeEach
    void setUp() {
        user = createUser();
        answer = createAnswer();
        friends = createFriends();
    }

    @Test
    @DisplayName("전체 질문 기록을 가져오는 메서드 테스트")
    void getAnswerRecordTest() {
        // given
        Users dummyUser = user;
        Answer dummyAnswer = answer;

        ReflectionTestUtils.setField(dummyAnswer, "createdAt", LocalDateTime.of(2024, 9, 19, 0, 0));

        Page<Answer> answerPage = new PageImpl<>(List.of(dummyAnswer), PageRequest.of(0, 10), 1);

        // when
        when(userReaderService.getUserById(anyLong())).thenReturn(dummyUser);
        when(answerReaderService.getAnswerList(any(Pageable.class), eq(dummyUser), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(answerPage);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());
        LocalDate testDate = LocalDate.of(2024, 9, 1);

        Page<AnswerModel.Record> response = answerService.getAnswerRecord(pageable, dummyUser.getId(), testDate);

        // then
        assertEquals(1, response.getContent().size());
        assertEquals(dummyAnswer.getId(), response.getContent().get(0).answerId());
        assertEquals(3, response.getContent().get(0).hintCount());
    }

    @Test
    @DisplayName("답변 새로고침 기능이 잘 동작하는지 확인하는 테스트")
    void refreshAnswerListTest() {
        // given
        Users dummyUser = user;
        List<Friend> dummyFriends = friends;

        when(userReaderService.getUserById(anyLong())).thenReturn(dummyUser);
        when(friendReaderService.getAllByHostUser(any(Users.class))).thenReturn(dummyFriends);

        // when
        AnswerModel.Refresh refreshResponse = answerService.refreshAnswerList(dummyUser.getId());

        // then
        assertEquals(5, refreshResponse.users().size());
        verify(friendReaderService, times(1)).getAllByHostUser(any(Users.class));
    }

    @Test
    @DisplayName("purchaseHint 메서드가 올바르게 작동하는지 테스트")
    void purchaseHintTest() {
        // given
        Users dummyUser = user;
        Answer dummyAnswer = mock(Answer.class);
        Long userId = dummyUser.getId();
        Long answerId = dummyAnswer.getId();

        AnswerCommand.Purchase command = mock(AnswerCommand.Purchase.class);
        when(command.answerId()).thenReturn(answerId);
        when(userReaderService.getUserById(userId)).thenReturn(dummyUser);
        when(answerReaderService.getAnswerById(answerId)).thenReturn(dummyAnswer);
        when(dummyAnswer.isNotPicked(dummyUser)).thenReturn(false);

        int decreasedPoint = 100;
        when(dummyUser.decreasePointsByHintCount(dummyAnswer.getHintCount())).thenReturn(decreasedPoint);

        // when
        answerService.purchaseHint(userId, command);

        // then
        verify(dummyUser, times(1)).decreasePointsByHintCount(dummyAnswer.getHintCount());
        verify(dummyAnswer, times(1)).increaseHintCount();
        verify(eventPublisher, times(1)).publishEvent(any(PointRecordEventDto.Earn.class));
    }

    private Users createUser() {
        return mock(Users.class);
    }

    private Answer createAnswer() {
        return Answer.builder()
                .id(1L)
                .question(mock(Question.class))
                .picker(user)
                .picked(user)
                .hintCount(3)
                .build();
    }

    private List<Friend> createFriends() {
        return  List.of(
                Friend.builder()
                        .hostUser(user)
                        .friendUser(Users.builder().id(2L).name("Friend 1").imageUrl("url1").build())
                        .build(),
                Friend.builder()
                        .hostUser(user)
                        .friendUser(Users.builder().id(3L).name("Friend 2").imageUrl("url2").build())
                        .build(),
                Friend.builder()
                        .hostUser(user)
                        .friendUser(Users.builder().id(4L).name("Friend 3").imageUrl("url3").build())
                        .build(),
                Friend.builder()
                        .hostUser(user)
                        .friendUser(Users.builder().id(5L).name("Friend 4").imageUrl("url4").build())
                        .build(),
                Friend.builder()
                        .hostUser(user)
                        .friendUser(Users.builder().id(6L).name("Friend 5").imageUrl("url5").build())
                        .build()
        );
    }
}