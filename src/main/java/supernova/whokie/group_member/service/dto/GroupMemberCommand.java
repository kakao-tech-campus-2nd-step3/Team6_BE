package supernova.whokie.group_member.service.dto;

import lombok.Builder;
import supernova.whokie.group.Groups;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.user.Users;

public class GroupMemberCommand {

    @Builder
    public record Create(Long groupId, Long userId, GroupRole role) {

        public GroupMember toEntity(Users user, Groups group) {
            return GroupMember.builder()
                .users(user)
                .groups(group)
                .groupRole(role)
                .groupStatus(GroupStatus.APPROVED)
                .build();
        }
    }
}
