package supernova.whokie.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.user.Users;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendWriterService {

    private final FriendRepository friendRepository;

    public void saveAll(List<Friend> friends) {
        friendRepository.saveAll(friends);
    }

    public void deleteAllByHostUser(Users host) {
        friendRepository.deleteAllByHostUser(host);
    }
}