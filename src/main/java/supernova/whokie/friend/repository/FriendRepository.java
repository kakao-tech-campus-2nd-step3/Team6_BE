package supernova.whokie.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.friend.Friend;
import supernova.whokie.user.Users;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f JOIN FETCH f.friendUser WHERE f.hostUser = :hostUser")
    List<Friend> findByHostUser(Users hostUser);
}
