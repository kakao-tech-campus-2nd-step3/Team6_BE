package supernova.whokie.group_member.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.friend.Friend;
import supernova.whokie.group_member.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);

    List<GroupMember> findAllByGroupId(Long groupId);

    @Query("SELECT g FROM GroupMember g WHERE g.user.id != :userId AND g.group.id = :groupId ORDER BY function('RAND')")
    List<GroupMember> getRandomGroupMember(@Param("userId") Long userId,
        @Param("groupId") Long groupId, Pageable pageable);

    Boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    Long countByGroupId(Long groupId);
}
