package supernova.whokie.answer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.service.dto.AnswerCommand;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.service.GroupReaderService;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.event.PointRecordEventDto;
import supernova.whokie.point_record.sevice.PointRecordWriterService;
import supernova.whokie.question.Question;
import supernova.whokie.question.service.QuestionReaderService;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserReaderService userReaderService;
    private final AnswerReaderService answerReaderService;
    private final QuestionReaderService questionReaderService;
    private final GroupReaderService groupReaderService;
    private final PointRecordWriterService pointRecordWriterService;
    private final AnswerWriterService answerWriterService;
    private final FriendReaderService friendReaderService;

    @Transactional(readOnly = true)
    public Page<AnswerModel.Record> getAnswerRecord(Pageable pageable, Long userId,
        LocalDate date) {
        Users user = userReaderService.getUserById(userId);

        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);

        // 해당 월의 데이터를 조회
        Page<Answer> answers = answerReaderService.getAnswerList(pageable, user, startDate,
            endDate);

        return answers.map(AnswerModel.Record::from);

    }

    @Transactional
    public void answerToCommonQuestion(Long userId, AnswerCommand.CommonAnswer command) {
        Users user = userReaderService.getUserById(userId);
        Question question = questionReaderService.getQuestionById(command.questionId());
        Users picked = userReaderService.getUserById(command.pickedId());

        Answer answer = command.toEntity(question, user, picked, Constants.DEFAULT_HINT_COUNT);
        answerWriterService.save(answer);

        user.increasePoint(Constants.ANSWER_POINT);
        eventPublisher.publishEvent(
            PointRecordEventDto.Earn.toDto(userId, Constants.ANSWER_POINT, 0,
                PointRecordOption.CHARGED,
                Constants.POINT_EARN_MESSAGE));

    }

    @Transactional
    public void answerToGroupQuestion(Long userId, AnswerCommand.Group command) {
        Users user = userReaderService.getUserById(userId);
        Question question = questionReaderService.getQuestionById(command.questionId());
        Users picked = userReaderService.getUserById(command.pickedId());
        Groups group = groupReaderService.getGroupById(command.groupId());

        checkGroupQuestion(question, group);

        Answer answer = command.toEntity(question, user, picked, Constants.DEFAULT_HINT_COUNT);
        answerWriterService.save(answer);

        user.increasePoint(Constants.ANSWER_POINT);

        var event = PointRecordEventDto.Earn.toDto(userId, Constants.ANSWER_POINT, 0,
            PointRecordOption.CHARGED,
            Constants.POINT_EARN_MESSAGE);

        eventPublisher.publishEvent(event);
    }

    @Transactional
    public void recordEarnPoint(PointRecordEventDto.Earn event) {
        PointRecord pointRecord = PointRecord.create(event.userId(), event.point(), event.amount(),
            event.option(), event.message());
        pointRecordWriterService.save(pointRecord);
    }

    @Transactional(readOnly = true)
    public AnswerModel.Refresh refreshAnswerList(Long userId) {
        Users user = userReaderService.getUserById(userId);

        List<Friend> allFriends = friendReaderService.getAllByHostUser(user);

        List<UserModel.PickedInfo> friendsInfoList = allFriends.stream().map(
            friend -> UserModel.PickedInfo.from(friend.getFriendUser())
        ).toList();

        return AnswerModel.Refresh.from(friendsInfoList);
    }

    @Transactional
    public void purchaseHint(Long userId, AnswerCommand.Purchase command) {
        Users user = userReaderService.getUserById(userId);
        Answer answer = answerReaderService.getAnswerById(command.answerId());

        validateIsPickedUser(answer, user);

        decreaseUserPoint(user, answer);
        answer.increaseHintCount();
    }

    //TODO 도메인 로직으로 넣어야함
    private void decreaseUserPoint(Users user, Answer answer) {
        switch (answer.getHintCount()) {
            case 1:
                checkUserHasNotEnoughPoint(user, Constants.FIRST_HINT_PURCHASE_POINT);
                user.decreasePoint(Constants.FIRST_HINT_PURCHASE_POINT);
                break;
            case 2:
                checkUserHasNotEnoughPoint(user, Constants.SECOND_HINT_PURCHASE_POINT);
                user.decreasePoint(Constants.SECOND_HINT_PURCHASE_POINT);
                break;
            case 3:
                checkUserHasNotEnoughPoint(user, Constants.THIRD_HINT_PURCHASE_POINT);
                user.decreasePoint(Constants.THIRD_HINT_PURCHASE_POINT);
                break;
        }
    }

    @Transactional(readOnly = true)
    public List<AnswerModel.Hint> getHints(Long userId, Long answerId) {
        Users user = userReaderService.getUserById(userId);
        Answer answer = answerReaderService.getAnswerById(answerId);
        validateIsPickedUser(answer, user);

        List<AnswerModel.Hint> allHints = new ArrayList<>();

        for (int i = 1; i <= Constants.MAX_HINT_COUNT; i++) {
            boolean valid = (i <= answer.getHintCount());
            allHints.add(AnswerModel.Hint.from(answer.getPicker(), i, valid));
        }

        return allHints;
    }

    //TODO 도메인 로직으로 넣어야함
    private void validateIsPickedUser(Answer answer, Users user) {
        if (isNotPicked(answer, user)) {
            throw new InvalidEntityException(MessageConstants.NOT_PICKED_USER_MESSAGE);
        }
    }

    private boolean isNotPicked(Answer answer, Users user) {
        return !answer.getPicked().getId().equals(user.getId());
    }

    private void checkGroupQuestion(Question question, Groups group) {
        if (question.isNotCorrectGroupQuestion(group.getId())) {
            throw new InvalidEntityException(MessageConstants.GROUP_NOT_FOUND_MESSAGE);
        }
    }

    private static void checkUserHasNotEnoughPoint(Users user, int hintPurchasePoint) {
        if (user.hasNotEnoughPoint(hintPurchasePoint)) {
            throw new InvalidEntityException(MessageConstants.NOT_ENOUGH_POINT_MESSAGE);
        }
    }
}
