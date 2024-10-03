package supernova.whokie.group.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import supernova.whokie.group.service.dto.GroupCommand;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;

public class GroupRequest {

    public record Create(
        @NotBlank
        String groupName,
        @NotBlank
        String groupDescription,
        @NotBlank
        String groupImageUrl
    ) {

        public GroupCommand.Create toCommand() {
            return GroupCommand.Create.builder()
                .groupName(groupName)
                .groupDescription(groupDescription)
                .groupImageUrl(groupImageUrl)
                .build();
        }

        public GroupMemberCommand.Create toGroupMemberCommand(Long groupId, Long userId) {
            return GroupMemberCommand.Create.builder()
                .role(GroupRole.LEADER)
                .groupId(groupId)
                .userId(userId)
                .build();
        }
    }

    public record Join(
        @NotNull
        Long groupId
    ) {

        public GroupMemberCommand.Create toGroupMemberCommand(Long groupId, Long userId) {
            return GroupMemberCommand.Create.builder()
                .role(GroupRole.MEMBER)
                .groupId(groupId)
                .userId(userId)
                .build();
        }
    }

    public record Exit(
        @NotNull
        Long groupId
    ) {

    }


    public record Modify(
        @NotNull
        Long groupId,
        @NotBlank
        String groupName,
        @NotBlank
        String groupDescription
    ) {

        public GroupCommand.Modify toCommand() {
            return GroupCommand.Modify.builder()
                .groupId(groupId)
                .groupName(groupName)
                .description(groupDescription)
                .build();
        }
    }
}
