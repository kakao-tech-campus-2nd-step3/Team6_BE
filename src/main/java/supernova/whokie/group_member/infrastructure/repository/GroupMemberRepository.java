package supernova.whokie.group_member.infrastructure.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supernova.whokie.group.Groups;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.user.Users;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByUsersIdAndGroupsId(Long userId, Long groupId);

    Optional<GroupMember> findByUsersIdAndGroupsId(Long userId, Long groupId);
}
