package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerCommand;
import supernova.whokie.answer.controller.dto.AnswerModel;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    public static final int FRIEND_LIMIT = 5;
    public static final int DEFAULT_HINT_COUNT = 0;

    private final AnswerRepository answerRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;


    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable, Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Page<Answer> answers = answerRepository.findAllByPicker(pageable, user);

        List<AnswerResponse.Record> answerResponse = answers.stream()
                .map(answer -> {
                    return AnswerResponse.Record.from(answer);
                })
                .toList();

        return PagingResponse.from(new PageImpl<>(answerResponse, pageable, answers.getTotalElements()));
    }

    public void answerToCommonQuestion(Long userId, AnswerCommand.CommonAnswer command) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
        Question question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));
        Users picked = userRepository.findById(command.pickedId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Answer answer = command.toEntity(question, user, picked);
        answerRepository.save(answer);
    }

    public AnswerModel.Refresh refreshAnswerList(Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Pageable pageable = PageRequest.of(0, FRIEND_LIMIT);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        List<UserResponse.PickedInfo> friendsInfoList = randomFriends.stream().map(
                friend -> UserResponse.PickedInfo.from(friend.getFriendUser())
        ).toList();

        return AnswerModel.Refresh.from(friendsInfoList);
    }
}
