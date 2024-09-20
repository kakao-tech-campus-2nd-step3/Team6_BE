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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService{

    public static final int FRIEND_LIMIT = 5;
    public static final int QUESTION_LIMIT = 10;

    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;

    public QuestionResponse.CommonQuestions getCommonQuestionList(Users user) {
        List<Question> allQuestionList = questionRepository.findAll();

        List<QuestionResponse.CommonQuestion> commonQuestions = getCommonQuestionList(user, allQuestionList, FRIEND_LIMIT, QUESTION_LIMIT);

        return QuestionResponse.CommonQuestions.builder()
                .questions(commonQuestions)
                .build();
    }


    private List<QuestionResponse.CommonQuestion> getCommonQuestionList(Users user, List<Question> allQuestionList, int friendLimit, int questionLimit) {

        List<Question> randomQuestions = allQuestionList.stream()
                .limit(questionLimit)
                .toList();

        List<Friend> allFriends = friendRepository.findAllFriendsByHostUser(user);
        Collections.shuffle(allFriends);

        List<QuestionResponse.CommonQuestion> commonQuestions = randomQuestions.stream()
                .map(question -> {
                    List<UserResponse.PickedInfo> friendList = getFriendList(allFriends, friendLimit);

                    return QuestionResponse.CommonQuestion.builder()
                            .questionId(question.getId())
                            .content(question.getContent())
                            .users(friendList)
                            .build();
                })
                .toList();
        return commonQuestions;
    }

    private List<UserResponse.PickedInfo> getFriendList(List<Friend> allFriends, int friendLimit) {
        List<Friend> randomFriends = allFriends.stream()
                .limit(friendLimit)
                .toList();

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
