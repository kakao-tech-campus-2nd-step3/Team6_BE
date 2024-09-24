package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerRecord;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    public static final int FRIEND_LIMIT = 5;

    private final AnswerRepository answerRepository;
    private final FriendRepository friendRepository;


    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable, Users user) {
        Page<Answer> answers = answerRepository.findAllByPicker(pageable, user);

        List<AnswerResponse.Record> answerResponse = answers.stream()
                .map(answer -> {
                    AnswerRecord answerRecord = AnswerRecord.from(answer);
                    return AnswerResponse.Record.from(answerRecord);
                })
                .toList();

        return PagingResponse.from(new PageImpl<>(answerResponse, pageable, answers.getTotalElements()));
    }

    public AnswerResponse.Refresh refreshAnswerList(Users user){
        Pageable pageable = PageRequest.of(0, FRIEND_LIMIT);
        List<Friend> randomFriends = friendRepository.findRandomFriendsByHostUser(user.getId(), pageable);

        List<UserResponse.PickedInfo> friendsInfoList = randomFriends.stream().map(
                friend -> UserResponse.PickedInfo.from(friend.getFriendUser())
        ).toList();

        return AnswerResponse.Refresh.builder()
                .users(friendsInfoList)
                .build();
    }
}
