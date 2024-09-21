package supernova.whokie.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.friend.Friend;
import supernova.whokie.user.Users;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f JOIN FETCH f.friendUser WHERE f.hostUser = :hostUser")
    List<Friend> findByHostUser(Users hostUser);

    @Query(value = "SELECT * FROM friend f WHERE f.host_user_id = :userId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Friend> findRandomFriendsByHostUser(@Param("userId") Long userId, @Param("limit") int limit);
}
