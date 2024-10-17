package supernova.whokie.friend.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;

@Service
@RequiredArgsConstructor
public class FriendWriterService {

    private final FriendRepository friendRepository;

    public void saveAll(List<Friend> friends) {
        friendRepository.saveAll(friends);
    }

    public void deleteAllById(List<Long> friends) {
        friendRepository.deleteByIdIn(friends);
    }

}
