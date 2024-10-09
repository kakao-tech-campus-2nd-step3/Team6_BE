package supernova.whokie.group.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.dto.GroupInfoWithMemberCount;

public interface GroupRepository extends JpaRepository<Groups, Long> {

    @Query(
        "SELECT new supernova.whokie.group.repository.dto.GroupInfoWithMemberCount("
            + "g.id, g.groupName, g.description, g.groupImageUrl, COUNT(m.id)) "
            + "FROM Groups g "
            + "JOIN GroupMember m ON g.id = m.group.id "
            + "WHERE g.id IN (SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId) "
            + "GROUP BY g.id"
    )
    Page<GroupInfoWithMemberCount> findGroupsWithMemberCountByUserId(Long userId,
        Pageable pageable);

    @Query(
        "SELECT new supernova.whokie.group.repository.dto.GroupInfoWithMemberCount("
            + "g.id, g.groupName, g.description, g.groupImageUrl, COUNT(m.id)) "
            + "FROM Groups g "
            + "JOIN GroupMember m ON g.id = m.group.id "
            + "WHERE g.id = :groupId "  // 특정 그룹 ID로 필터링
            + "GROUP BY g.id"  // 모든 필드를 그룹화에 포함
    )
    Optional<GroupInfoWithMemberCount> findGroupInfoWithMemberCountByGroupId(Long groupId);

}
