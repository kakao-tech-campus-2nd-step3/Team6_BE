package supernova.whokie.group_member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supernova.whokie.group_member.GroupMember;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

}
