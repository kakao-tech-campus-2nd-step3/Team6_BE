package supernova.whokie.group.controller.dto;

import lombok.Builder;
import supernova.whokie.group.service.dto.GroupModel;

public class GroupResponse {

    @Builder
    public record InfoWithCount(
        Long groupId,
        String groupName,
        String groupImageUrl,
        String groupDescription,
        Long groupMemberCount
    ) {

        public static InfoWithCount from(GroupModel.InfoWithMemberCount groupModel) {
            return InfoWithCount.builder()
                .groupId(groupModel.groupId())
                .groupName(groupModel.groupName())
                .groupImageUrl(groupModel.groupImageUrl())
                .groupDescription(groupModel.groupDescription())
                .groupMemberCount(groupModel.groupMemberCount())
                .build();
        }
    }

    @Builder
    public record InviteCode(
        String inviteCode
    ) {

        public static InviteCode from(GroupModel.InviteCode model) {
            return InviteCode.builder()
                .inviteCode(model.inviteCode())
                .build();
        }
    }

    @Builder
    public record Info(
        Long groupId,
        String groupName,
        String groupDescription,
        String groupImageUrl
    ) {

        public static Info from(GroupModel.Info model) {
            return Info.builder()
                .groupId(model.groupId())
                .groupName(model.groupName())
                .groupDescription(model.groupDescription())
                .groupImageUrl(model.groupImageUrl())
                .build();
        }
    }
}
