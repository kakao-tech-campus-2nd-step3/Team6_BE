package supernova.whokie.friend.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
