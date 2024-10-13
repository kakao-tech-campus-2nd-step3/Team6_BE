package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.answer.service.dto.AnswerCommand;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.event.PointRecordEventDto;
import supernova.whokie.point_record.infrastructure.repository.PointRecordRepository;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PointRecordRepository pointRecordRepository;
    private final GroupRepository groupsRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        Page<Answer> answers = answerRepository.findAllByPicker(pageable, user);

        List<AnswerResponse.Record> answerResponse = answers.stream()
                .map(AnswerResponse.Record::from)
                .toList();

        return PagingResponse.from(
                new PageImpl<>(answerResponse, pageable, answers.getTotalElements()));
    }

    @Transactional
    public void answerToCommonQuestion(Long userId, AnswerCommand.CommonAnswer command) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
        Question question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.QUESTION_NOT_FOUND_MESSAGE));
        Users picked = userRepository.findById(command.pickedId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        Answer answer = command.toEntity(question, user, picked, Constants.DEFAULT_HINT_COUNT);
        answerRepository.save(answer);

        user.increasePoint(Constants.ANSWER_POINT);
        eventPublisher.publishEvent(
                PointRecordEventDto.Earn.toDto(userId, Constants.ANSWER_POINT, 0, PointRecordOption.CHARGED,
                        Constants.POINT_EARN_MESSAGE));

    }

    @Transactional
    public void answerToGroupQuestion(Long userId, AnswerCommand.Group command) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        Question question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.QUESTION_NOT_FOUND_MESSAGE));

        Users picked = userRepository.findById(command.pickedId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        Groups group = groupsRepository.findById(command.groupId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_NOT_FOUND_MESSAGE));


        checkGroupQuestion(question, group);

        Answer answer = command.toEntity(question, user, picked, Constants.DEFAULT_HINT_COUNT);
        answerRepository.save(answer);

        user.increasePoint(Constants.ANSWER_POINT);
        eventPublisher.publishEvent(
                PointRecordEventDto.Earn.toDto(userId, Constants.ANSWER_POINT, 0, PointRecordOption.CHARGED,
                        Constants.POINT_EARN_MESSAGE));
    }

    public void recordEarnPoint(PointRecordEventDto.Earn event) {
        PointRecord pointRecord = PointRecord.create(event.userId(), event.point(), event.amount(),
                event.option(), event.message());
        pointRecordRepository.save(pointRecord);
    }

    @Transactional(readOnly = true)
    public AnswerModel.Refresh refreshAnswerList(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
        List<Friend> allFriends = friendRepository.findAllByHostUser(user);

        List<UserModel.PickedInfo> friendsInfoList = allFriends.stream().map(
                friend -> UserModel.PickedInfo.from(friend.getFriendUser())
        ).toList();

        return AnswerModel.Refresh.from(friendsInfoList);
    }

    @Transactional
    public void purchaseHint(Long userId, AnswerCommand.Purchase command) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
        Answer answer = answerRepository.findById(command.answerId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.ANSWER_NOT_FOUND_MESSAGE));

        validateIsPickedUser(answer, user);

        decreaseUserPoint(user, answer);
        answer.increaseHintCount();
    }

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
    public List<AnswerModel.Hint> getHints(Long userId, String answerId) {
        Long parsedAnswerId = Long.parseLong(answerId);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
        Answer answer = answerRepository.findById(parsedAnswerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.ANSWER_NOT_FOUND_MESSAGE));

        validateIsPickedUser(answer, user);

        List<AnswerModel.Hint> allHints = new ArrayList<>();

        for (int i = 1; i <= Constants.MAX_HINT_COUNT; i++) {
            boolean valid = (i <= answer.getHintCount());
            allHints.add(AnswerModel.Hint.from(answer.getPicker(), i, valid));
        }

        return allHints;
    }

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
            System.out.println("질문 아이디 " + question.getId());
            System.out.println("그룹 아이디 " + group.getId());
            throw new InvalidEntityException(MessageConstants.GROUP_NOT_FOUND_MESSAGE);
        }
    }

    private static void checkUserHasNotEnoughPoint(Users user, int hintPurchasePoint) {
        if (user.hasNotEnoughPoint(hintPurchasePoint)) {
            throw new InvalidEntityException(MessageConstants.NOT_ENOUGH_POINT_MESSAGE);
        }
    }
}
