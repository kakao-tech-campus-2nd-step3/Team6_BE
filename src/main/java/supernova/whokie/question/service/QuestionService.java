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
public class QuestionService{

    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;

    public QuestionResponse.CommonQuestions getCommonQuestionList(Users user) {
        int friendLimit = 5;
        int questionLimit = 10;

        List<QuestionResponse.CommonQuestion> commonQuestions = getCommonQuestionList(user, friendLimit, questionLimit);

        return QuestionResponse.CommonQuestions.builder()
                .questions(commonQuestions)
                .build();
    }


    private List<QuestionResponse.CommonQuestion> getCommonQuestionList(Users user, int friendLimit, int questionLimit) {
        List<Question> questions = questionRepository.findRandomQuestions(questionLimit);

        List<QuestionResponse.CommonQuestion> commonQuestions = questions.stream()
                .map(question -> {
                    List<UserResponse.PickedInfo> friendList = getFriendList(user, friendLimit);

                    return QuestionResponse.CommonQuestion.builder()
                            .questionId(question.getId())
                            .content(question.getContent())
                            .users(friendList)
                            .build();
                })
                .toList();
        return commonQuestions;
    }

    private List<UserResponse.PickedInfo> getFriendList(Users user, int friendLimit) {
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), friendLimit);

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
