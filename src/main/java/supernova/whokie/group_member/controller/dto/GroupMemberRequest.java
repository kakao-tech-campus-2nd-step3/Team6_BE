package supernova.whokie.group_member.controller.dto;

import supernova.whokie.group_member.service.dto.GroupMemberCommand;

public class GroupMemberRequest {

    public record Modify(
            Long groupId,
            Long pastLeaderId,
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
            Long groupId,
            Long userId
    ) {

    }
}
