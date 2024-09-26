package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.question.Question;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    @Value("${friend-limit}")
    private int friendLimit;

    @Value("${question-limit}")
    private int questionLimit;

    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<QuestionModel.CommonQuestion> getCommonQuestion(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        return getCommonQuestionList(user);
    }


    private List<QuestionModel.CommonQuestion> getCommonQuestionList(Users user) {
        Pageable pageable = PageRequest.of(0, questionLimit);

        List<Question> randomQuestions = questionRepository.findRandomQuestions(pageable);

        return randomQuestions.stream()
                .map(question -> QuestionModel.CommonQuestion.from(question, getFriendList(user)))
                .toList();
    }

    private List<UserResponse.PickedInfo> getFriendList(Users user) {
        Pageable pageable = PageRequest.of(0, friendLimit);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        return randomFriends.stream()
                .map(friend -> UserResponse.PickedInfo.from(friend.getFriendUser()))
                .toList();
    }

}
