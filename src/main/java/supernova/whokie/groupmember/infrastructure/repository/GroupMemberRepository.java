package supernova.whokie.groupmember.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.groupmember.GroupMember;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT g FROM GroupMember g WHERE g.group.id = :groupId")
    Page<GroupMember> findAllByGroupId(Pageable pageable, Long groupId);

    @Query("SELECT g FROM GroupMember g JOIN FETCH g.user WHERE g.user.id != :userId AND g.group.id = :groupId ORDER BY function('RAND')")
    List<GroupMember> getRandomGroupMemberJoinFetch(@Param("userId") Long userId,
                                           @Param("groupId") Long groupId, Pageable pageable);

    Boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    Long countByGroupId(Long groupId);

}
