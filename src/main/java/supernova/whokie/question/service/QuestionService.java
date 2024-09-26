package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.question.Question;
import supernova.whokie.question.controller.dto.QuestionModel;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    public static final int FRIEND_LIMIT = 5;
    public static final int QUESTION_LIMIT = 10;

    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public List<QuestionModel.CommonQuestion> getCommonQuestion(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        List<QuestionModel.CommonQuestion> commonQuestions = getCommonQuestionList(user);

        return commonQuestions;
    }


    private List<QuestionModel.CommonQuestion> getCommonQuestionList(Users user) {
        Pageable pageable = PageRequest.of(0, QUESTION_LIMIT);

        List<Question> randomQuestions = questionRepository.findRandomQuestions(pageable);

        List<QuestionModel.CommonQuestion> commonQuestions = randomQuestions.stream()
                .map(question -> {
                    List<UserResponse.PickedInfo> friendList = getFriendList(user);

                    return QuestionModel.CommonQuestion.from(question, friendList);
                })
                .toList();
        return commonQuestions;
    }

    private List<UserResponse.PickedInfo> getFriendList(Users user) {
        Pageable pageable = PageRequest.of(0, FRIEND_LIMIT);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        List<UserResponse.PickedInfo> friendList = randomFriends.stream()
                .map(friend -> UserResponse.PickedInfo.from(friend.getFriendUser()))
                .toList();
        return friendList;
    }

}
