package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.repository.FriendRepository;
import supernova.whokie.question.Question;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    public static final int FRIEND_LIMIT = 5;
    public static final int QUESTION_LIMIT = 10;

    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;

    public QuestionResponse.CommonQuestions getCommonQuestion(Users user) {


        List<QuestionResponse.CommonQuestion> commonQuestions = getCommonQuestionList(user);

        return QuestionResponse.CommonQuestions.builder()
                .questions(commonQuestions)
                .build();
    }


    private List<QuestionResponse.CommonQuestion> getCommonQuestionList(Users user) {
        Pageable pageable = PageRequest.of(0, QUESTION_LIMIT);

        List<Question> randomQuestions = questionRepository.findRandomQuestions(pageable);

        List<QuestionResponse.CommonQuestion> commonQuestions = randomQuestions.stream()
                .map(question -> {
                    List<UserResponse.PickedInfo> friendList = getFriendList(user);

                    return QuestionResponse.CommonQuestion.from(question, friendList);
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
