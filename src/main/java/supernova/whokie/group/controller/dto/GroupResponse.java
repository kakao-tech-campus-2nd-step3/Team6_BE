package supernova.whokie.group.controller.dto;

import lombok.Builder;
import supernova.whokie.group.service.dto.GroupModel;

public class GroupResponse {

    @Builder
    public record Info(
        Long groupId,
        String groupName,
        String groupImageUrl
    ) {

    }

    @Builder
    public record InfoWithMemberCount(
        Long groupId,
        String groupName,
        String groupImageUrl,
        Long groupMemberCount
    ) {

        public static InfoWithMemberCount from(GroupModel.InfoWithMemberCount infoWithMemberCount) {
            return InfoWithMemberCount.builder()
                .groupId(infoWithMemberCount.groupId())
                .groupName(infoWithMemberCount.groupName())
                .groupImageUrl(infoWithMemberCount.groupImageUrl())
                .groupMemberCount(infoWithMemberCount.groupMemberCount())
                .build();
        }
    }
}
