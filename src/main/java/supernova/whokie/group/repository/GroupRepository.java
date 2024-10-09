package supernova.whokie.group.repository;

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
            + "WHERE m.user.id = :userId "
            + "GROUP BY g.id"
    )
    Page<GroupInfoWithMemberCount> findGroupsWithMemberCountByUserId(Long userId,
        Pageable pageable);
}
