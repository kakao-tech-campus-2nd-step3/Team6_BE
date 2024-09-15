package supernova.whokie.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.friend.Friend;
import supernova.whokie.user.Users;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByHostUser(Users hostUser);
}
