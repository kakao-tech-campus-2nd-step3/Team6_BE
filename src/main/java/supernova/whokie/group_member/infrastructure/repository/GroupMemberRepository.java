package supernova.whokie.group_member.infrastructure.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.group_member.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);
}
