package supernova.whokie.group_member.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GroupMemberRequest {

    public record Modify(
            @NotNull @Min(1)
            Long groupId,
            @NotNull @Min(1)
            Long pastLeaderId,
            @NotNull @Min(1)
            Long newLeaderId
    ) {

    }

    public record Expel(
            @NotNull @Min(1)
            Long groupId,
            @NotNull @Min(1)
            Long userId
    ) {

    }
}
