package supernova.whokie.group_member.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;

public class GroupMemberRequest {

    public record Modify(
            @NotNull
            @Positive
            Long groupId,
            @NotNull
            @Positive
            Long pastLeaderId,
            @NotNull
            @Positive
            Long newLeaderId
    ) {

        public GroupMemberCommand.Modify toCommand() {
            return GroupMemberCommand.Modify.builder()
                .groupId(groupId)
                .pastLeaderId(pastLeaderId)
                .newLeaderId(newLeaderId)
                .build();
        }
    }

    public record Expel(
            @NotNull
            @Positive
            Long groupId,
            @NotNull
            @Positive
            Long userId
    ) {

        public GroupMemberCommand.Expel toCommand() {
            return GroupMemberCommand.Expel.builder()
                .groupId(groupId)
                .userId(userId)
                .build();
        }
    }
}
