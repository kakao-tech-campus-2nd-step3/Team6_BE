package supernova.whokie.friend.infrastructure.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.friend.Friend;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f JOIN FETCH f.friendUser WHERE f.hostUser.id = :hostUserId")
    List<Friend> findByHostUserIdFetchJoin(Long hostUserId);

    void deleteByIdIn(List<Long> ids);
    @Query("SELECT f FROM Friend f WHERE f.hostUser.id = :userId ORDER BY function('RAND')")
    List<Friend> findRandomFriendsByHostUser(@Param("userId") Long userId, Pageable pageable);
}
