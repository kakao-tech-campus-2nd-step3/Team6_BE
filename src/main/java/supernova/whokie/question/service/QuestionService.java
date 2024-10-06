package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupsRepository;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {


    private final QuestionRepository questionRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final GroupsRepository groupsRepository;

    @Transactional(readOnly = true)
    public List<QuestionModel.CommonQuestion> getCommonQuestion(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        return getCommonQuestionList(user);
    }



    @Transactional(readOnly = true)
    public Page<QuestionModel.Info> getGroupQuestionPaging(Long userId, String groupId, Boolean status, Pageable pageable) {
        Long parsedGroupId = Long.parseLong(groupId);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        Groups group = groupsRepository.findById(parsedGroupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹이 존재하지 않습니다."));

        List<Question> groupQuestions = questionRepository.findAllByGroupId(parsedGroupId);

        return getGroupQuestionsByStatus(status, groupQuestions, pageable);
    }

    private static Page<QuestionModel.Info> getGroupQuestionsByStatus(Boolean status, List<Question> groupQuestions, Pageable pageable) {
        List<Question> filteredQuestions;


        if (Boolean.TRUE.equals(status)) {
            filteredQuestions = groupQuestions.stream()
                    .filter(question -> question.getQuestionStatus().equals(QuestionStatus.APPROVED))
                    .toList();
        } else if (Boolean.FALSE.equals(status)) {
            filteredQuestions = groupQuestions.stream()
                    .filter(question -> question.getQuestionStatus().equals(QuestionStatus.REJECTED))
                    .toList();
        } else {
            filteredQuestions = new ArrayList<>();
        }


        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredQuestions.size());
        List<QuestionModel.Info> result = filteredQuestions.subList(start, end).stream()
                .map(question -> QuestionModel.Info.from(question, status))
                .toList();

        return new PageImpl<>(result, pageable, filteredQuestions.size());
    }


    private List<QuestionModel.CommonQuestion> getCommonQuestionList(Users user) {
        Pageable pageable = PageRequest.of(0, Constants.QUESTION_LIMIT);

        List<Question> randomQuestions = questionRepository.findRandomQuestions(pageable);

        return randomQuestions.stream()
                .map(question -> QuestionModel.CommonQuestion.from(question, getFriendList(user)))
                .toList();
    }

    private List<UserModel.PickedInfo> getFriendList(Users user) {
        Pageable pageable = PageRequest.of(0, Constants.FRIEND_LIMIT);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        return randomFriends.stream()
                .map(friend -> UserModel.PickedInfo.from(friend.getFriendUser()))
                .toList();
    }


}
