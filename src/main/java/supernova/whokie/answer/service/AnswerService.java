package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Constants;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.alarm.event.AlarmEventDto;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.constants.AnswerConstants;
import supernova.whokie.answer.service.dto.AnswerCommand;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.FriendReaderService;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.service.GroupReaderService;
import supernova.whokie.pointrecord.PointRecordOption;
import supernova.whokie.pointrecord.constants.PointConstants;
import supernova.whokie.pointrecord.event.PointRecordEventDto;
import supernova.whokie.question.Question;
import supernova.whokie.question.service.QuestionReaderService;
import supernova.whokie.ranking.service.RankingWriterService;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;
import supernova.whokie.user.service.dto.UserModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserReaderService userReaderService;
    private final AnswerReaderService answerReaderService;
    private final QuestionReaderService questionReaderService;
    private final GroupReaderService groupReaderService;
    private final AnswerWriterService answerWriterService;
    private final FriendReaderService friendReaderService;
    private final S3Service s3Service;
    private final RankingWriterService rankingWriterService;

    @Transactional(readOnly = true)
    public Page<AnswerModel.Record> getAnswerRecord(Pageable pageable, Long userId, LocalDate date) {
        Users user = userReaderService.getUserById(userId);

        LocalDateTime startDate;
        LocalDateTime endDate;

        if (date == null) {
            startDate = AnswerConstants.DEFAULT_START_DATE;
            endDate = LocalDateTime.now();
        } else {
            startDate = date.atStartOfDay();
            endDate = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
        }

        // 지정된 기간 내의 데이터를 조회
        Page<Answer> answers = answerReaderService.getAnswerList(pageable, user, startDate, endDate);
        return answers.map(AnswerModel.Record::from);
    }
    @Transactional
    public void answerToCommonQuestion(Long userId, AnswerCommand.CommonAnswer command) {
        Question question = questionReaderService.getQuestionById(command.questionId());

        answerToQuestion(userId, command.pickedId(), question);
    }

    @Transactional
    public void answerToGroupQuestion(Long userId, AnswerCommand.Group command) {
        Question question = questionReaderService.getQuestionById(command.questionId());
        if(question.isNotCorrectGroupQuestion(command.groupId())) {
            throw new InvalidEntityException(MessageConstants.GROUP_NOT_FOUND_MESSAGE);
        }

        answerToQuestion(userId, command.pickedId(), question);
    }

    @Transactional(readOnly = true)
    public AnswerModel.Refresh refreshAnswerList(Long userId) {
        Users user = userReaderService.getUserById(userId);

        List<Friend> allFriends = friendReaderService.getAllByHostUser(user);

        List<UserModel.PickedInfo> friendsInfoList = allFriends.stream()
                .map(friend -> {
                    String imageUrl = friend.getFriendUser().getImageUrl();
                    if (user.isImageUrlStoredInS3()) {
                        imageUrl = s3Service.getSignedUrl(imageUrl);
                    }
                    return UserModel.PickedInfo.from(friend.getFriendUser(), imageUrl);
                }).toList();

        return AnswerModel.Refresh.from(friendsInfoList);
    }

    @Transactional
    public void purchaseHint(Long userId, AnswerCommand.Purchase command) {
        Users user = userReaderService.getUserById(userId);
        Answer answer = answerReaderService.getAnswerById(command.answerId());

        if (answer.isNotPicked(user)){
            throw new InvalidEntityException(MessageConstants.NOT_PICKED_USER_MESSAGE);
        }

        user.decreasePointsByHintCount(answer);

        answer.increaseHintCount();
    }

    @Transactional(readOnly = true)
    public List<AnswerModel.Hint> getHints(Long userId, Long answerId) {
        Users user = userReaderService.getUserById(userId);
        Answer answer = answerReaderService.getAnswerById(answerId);

        if (answer.isNotPicked(user)){
            throw new InvalidEntityException(MessageConstants.NOT_PICKED_USER_MESSAGE);
        }

        List<AnswerModel.Hint> allHints = new ArrayList<>();

        for (int i = 1; i <= AnswerConstants.MAX_HINT_COUNT; i++) {
            boolean valid = (i <= answer.getHintCount());
            allHints.add(AnswerModel.Hint.from(answer, i, valid));
        }

        return allHints;
    }

    private void answerToQuestion(Long userId, Long pickedId, Question question) {
        Users user = userReaderService.getUserById(userId);
        Users picked = userReaderService.getUserById(pickedId);
        Groups group = groupReaderService.getGroupById(question.getGroupId());

        Answer answer = Answer.create(question, user, picked, AnswerConstants.DEFAULT_HINT_COUNT);
        answerWriterService.save(answer);

        // Ranking Count 증가
        rankingWriterService.increaseRankingCountByUserAndQuestionAndGroups(user, question.getContent(), group);
        user.increasePoint(AnswerConstants.ANSWER_POINT);

        // 웹 알림 전송
        AlarmEventDto.Alarm alarmEvent = AlarmEventDto.Alarm.toDto(picked.getId(), question.getContent());
        eventPublisher.publishEvent(alarmEvent);

        // 포인트 기록
        PointRecordEventDto.Earn pointEvent = PointRecordEventDto.Earn.toDto(userId, AnswerConstants.ANSWER_POINT, 0,
                PointRecordOption.CHARGED, PointConstants.POINT_EARN_MESSAGE);
        eventPublisher.publishEvent(pointEvent);
    }
}
