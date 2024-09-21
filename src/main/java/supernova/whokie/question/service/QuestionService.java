package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
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

        List<Question> randomQuestions = questionRepository.findRandomQuestions(QUESTION_LIMIT);

        List<QuestionResponse.CommonQuestion> commonQuestions = randomQuestions.stream()
                .map(question -> {
                    List<UserResponse.PickedInfo> friendList = getFriendList(user);

                    return QuestionResponse.CommonQuestion.builder()
                            .questionId(question.getId())
                            .content(question.getContent())
                            .users(friendList)
                            .build();
                })
                .toList();
        return commonQuestions;
    }

    private List<UserResponse.PickedInfo> getFriendList(Users user) {
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), FRIEND_LIMIT);

        List<UserResponse.PickedInfo> friendList = randomFriends.stream()
                .map(friend -> UserResponse.PickedInfo.builder()
                        .userId(friend.getFriendUser().getId())
                        .name(friend.getFriendUser().getName())
                        .imageUrl(friend.getFriendUser().getImageUrl())
                        .build())
                .toList();
        return friendList;
    }

}
