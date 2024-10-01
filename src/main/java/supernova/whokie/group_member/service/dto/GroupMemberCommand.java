package supernova.whokie.group_member.service.dto;

import lombok.Builder;

public class GroupMemberCommand {

    @Builder
    public record Modify(
        Long groupId,
        Long pastLeaderId,
        Long newLeaderId
    ) {

    }

    @Builder
    public record Expel(
        Long groupId,
        Long userId
    ) {

    }
}
