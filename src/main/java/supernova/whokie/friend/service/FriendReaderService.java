package supernova.whokie.friend.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.user.Users;

@Service
@RequiredArgsConstructor
public class FriendReaderService {

    private final FriendRepository friendRepository;

    public List<Friend> getAllByHostUser(Users user) {
        return friendRepository.findAllByHostUser(user);
    }

    @Transactional(readOnly = true)
    public Set<Long> getFriendIdsByHostUser(Long userId) {
        List<Friend> existingList = friendRepository.findByHostUserIdFetchJoin(userId);
        return extractFriendUserIdAsSet(existingList);

    }

    @Transactional(readOnly = true)
    public List<Friend> findByHostUserIdFetchJoin(Long userId) {
        return friendRepository.findByHostUserIdFetchJoin(userId);
    }

    public Set<Long> extractFriendUserIdAsSet(List<Friend> friends) {
        return friends.stream().map(Friend::getFriendUserId).collect(Collectors.toSet());
    }
}
