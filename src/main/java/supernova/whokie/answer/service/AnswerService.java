package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.event.PointRecordEventDto;
import supernova.whokie.point_record.infrastructure.repository.PointRecordRepository;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UsersRepository;


import java.util.ArrayList;
import java.util.List;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FriendRepository friendRepository;
    private final UsersRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PointRecordRepository pointRecordRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${answer-point}")
    private int answerPoint;
    @Value("${default-hint-count}")
    private int defaultHintCount;
    @Value("${point-earn-message}")
    private String pointEarnMessage;
    @Value("${max-hint-count}")
    private int maxHintCount;
    @Value("${first-hint-purchase-point}")
    private int firstHintPurchasePoint;
    @Value("${second-hint-purchase-point}")
    private int secondHintPurchasePoint;
    @Value("${third-hint-purchase-point}")
    private int thirdHintPurchasePoint;

    @Transactional(readOnly = true)
    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable, Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

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
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
        Question question = questionRepository.findById(command.questionId())
            .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));
        Users picked = userRepository.findById(command.pickedId())
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Answer answer = command.toEntity(question, user, picked, defaultHintCount);
        answerRepository.save(answer);

        user.increasePoint(answerPoint);
        eventPublisher.publishEvent(
            PointRecordEventDto.Earn.toDto(userId, answerPoint, 0, PointRecordOption.CHARGED,
                pointEarnMessage));

    }

    public void recordEarnPoint(PointRecordEventDto.Earn event) {
        PointRecord pointRecord = PointRecord.create(event.userId(), event.point(), event.amount(),
            event.option(), event.message());
        pointRecordRepository.save(pointRecord);
    }

    @Transactional(readOnly = true)
    public AnswerModel.Refresh refreshAnswerList(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        List<Friend> allFriends = friendRepository.findAllByHostUser(user);

        List<UserModel.PickedInfo> friendsInfoList = allFriends.stream().map(
            friend -> UserModel.PickedInfo.from(friend.getFriendUser())
        ).toList();

        return AnswerModel.Refresh.from(friendsInfoList);
    }

    @Transactional
    public void purchaseHint(Long userId, AnswerCommand.Purchase command) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
        Answer answer = answerRepository.findById(command.answerId())
            .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        validateIsPickedUser(answer, user);

        decreaseUserPoint(user, answer);
        answer.increaseHintCount();
    }

    private void decreaseUserPoint(Users user, Answer answer) {
        switch (answer.getHintCount()) {
            case 1:
                checkUserHasNotEnoughPoint(user, firstHintPurchasePoint);
                user.decreasePoint(firstHintPurchasePoint);
                break;
            case 2:
                checkUserHasNotEnoughPoint(user, secondHintPurchasePoint);
                user.decreasePoint(secondHintPurchasePoint);
                break;
            case 3:
                checkUserHasNotEnoughPoint(user, thirdHintPurchasePoint);
                user.decreasePoint(thirdHintPurchasePoint);
                break;
        }
    }

    private static void checkUserHasNotEnoughPoint(Users user, int hintPurchasePoint) {
        if (user.hasNotEnoughPoint(hintPurchasePoint)) {
            throw new InvalidEntityException("포인트가 부족합니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<AnswerModel.Hint> getHints(Long userId, String answerId) {
        Long parsedAnswerId = Long.parseLong(answerId);
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
        Answer answer = answerRepository.findById(parsedAnswerId)
            .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        validateIsPickedUser(answer, user);

        List<AnswerModel.Hint> allHints = new ArrayList<>();

        for (int i = 1; i <= maxHintCount; i++) {
            boolean valid = (i <= answer.getHintCount());
            allHints.add(AnswerModel.Hint.from(answer.getPicker(), i, valid));
        }

        return allHints;
    }

    private void validateIsPickedUser(Answer answer, Users user) {
        if (isNotPicked(answer, user)) {
            throw new InvalidEntityException("해당 답변의 picked유저가 아닙니다.");
        }
    }

    private boolean isNotPicked(Answer answer, Users user) {
        return !answer.getPicked().getId().equals(user.getId());
    }
}
