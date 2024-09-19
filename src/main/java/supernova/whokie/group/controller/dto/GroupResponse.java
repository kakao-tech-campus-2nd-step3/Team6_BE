package supernova.whokie.group.controller.dto;

import lombok.Builder;

public class GroupResponse {

    @Builder
    public record Info(
            Long groupId,
            String groupName,
            String groupImageUrl,
            int groupMemberCount
    ) {

    }
}
