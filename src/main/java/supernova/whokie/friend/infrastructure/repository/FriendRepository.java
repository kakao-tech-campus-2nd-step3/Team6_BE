package supernova.whokie.friend.infrastructure.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.friend.Friend;
import supernova.whokie.user.Users;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f JOIN FETCH f.friendUser WHERE f.hostUser.id = :hostUserId")
    List<Friend> findByHostUserIdFetchJoin(Long hostUserId);

    void deleteByIdIn(List<Long> ids);

    List<Friend> findAllByHostUser(Users user);
}
