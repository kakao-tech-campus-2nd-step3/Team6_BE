package supernova.whokie.group_member.controller.dto;

public class GroupMemberRequest {

    public record Modify(
            Long groupId,
            Long pastLeaderId,
            Long newLeaderId
    ) {

    }

    public record Expel(
            Long groupId,
            Long userId
    ) {

    }
}
