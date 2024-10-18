package supernova.whokie.group.controller.dto;

import lombok.Builder;
import supernova.whokie.group.service.dto.GroupModel;

public class GroupResponse {

    @Builder
    public record Info(
            Long groupId,
            String groupName,
            String groupImageUrl,
            Long groupMemberCount
    ) {

        public static Info from(GroupModel.InfoWithMemberCount groupModel) {
            return Info.builder()
                    .groupId(groupModel.groupId())
                    .groupName(groupModel.groupName())
                    .groupImageUrl(groupModel.groupImageUrl())
                    .groupMemberCount(groupModel.groupMemberCount())
                    .build();
        }
    }
}
