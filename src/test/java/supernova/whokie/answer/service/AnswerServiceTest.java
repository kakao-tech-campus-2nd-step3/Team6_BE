package supernova.whokie.answer.service;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import org.springframework.test.util.ReflectionTestUtils;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.answer.service.dto.AnswerCommand;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.group.service.GroupReaderService;
import supernova.whokie.point_record.event.PointRecordEventDto;
import supernova.whokie.point_record.sevice.PointRecordWriterService;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.question.service.QuestionReaderService;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.UserReaderService;

import java.time.LocalDate;
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
    "jwt.secret=abcd"
})
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AnswerServiceTest {

    @MockBean
    private AnswerReaderService answerReaderService;

    @MockBean
    private UserReaderService userReaderService;

    @MockBean
    private FriendReaderService friendReaderService;

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
                .picker(dummyUser)
                .picked(mock(Users.class))
                .hintCount(3)
                .build();

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

        Users user = mock(Users.class);
        Answer answer = mock(Answer.class);
        // given
        Long userId = 1L;
        Long answerId = 1L;

        AnswerCommand.Purchase command = mock(AnswerCommand.Purchase.class);
        when(command.answerId()).thenReturn(answerId);
        when(userReaderService.getUserById(userId)).thenReturn(user);
        when(answerReaderService.getAnswerById(answerId)).thenReturn(answer);
        when(answer.isNotPicked(user)).thenReturn(false);

        // when
        answerService.purchaseHint(userId, command);

        // then
        verify(user, times(1)).decreasePointsByHintCount(answer);
        verify(answer, times(1)).increaseHintCount();
    }


}