package supernova.whokie.group.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupModel;

public interface GroupsRepository extends JpaRepository<Groups, Long> {

    @Query(
        "SELECT new supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount("
            +
            "g.id, g.groupName, g.description, g.groupImageUrl, COUNT(m.id)) " +
            "FROM Groups g " +
            "LEFT JOIN GroupMember m ON g.id = m.groups.id " +
            "GROUP BY g.id")
    Page<GroupInfoWithMemberCount> findGroupsWithMemberCount(Pageable pageable);
}
