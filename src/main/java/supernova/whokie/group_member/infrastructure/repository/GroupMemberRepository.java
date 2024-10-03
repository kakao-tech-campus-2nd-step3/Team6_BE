package supernova.whokie.group_member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supernova.whokie.group.Groups;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.user.Users;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByUsersAndGroups(Users user, Groups group);
}
