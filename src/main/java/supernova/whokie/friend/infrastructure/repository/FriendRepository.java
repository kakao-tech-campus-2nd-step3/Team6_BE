package supernova.whokie.friend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.friend.Friend;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f JOIN FETCH f.friendUser WHERE f.hostUser.id = :hostUserId")
    List<Friend> findByHostUserIdFetchJoin(Long hostUserId);

    void deleteByIdIn(List<Long> ids);
}
