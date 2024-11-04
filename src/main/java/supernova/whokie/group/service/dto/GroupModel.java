package supernova.whokie.group.service.dto;

import lombok.Builder;
import supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount;

public class GroupModel {

    @Builder
    public record InfoWithMemberCount(
        Long groupId,
        String groupName,
        String groupDescription,
        String groupImageUrl,
        Long groupMemberCount
    ) {

        public static InfoWithMemberCount from(GroupInfoWithMemberCount groupInfoWithMemberCount, String imageUrl) {
            return InfoWithMemberCount.builder()
                .groupId(groupInfoWithMemberCount.getGroupId())
                .groupName(groupInfoWithMemberCount.getGroupName())
                .groupDescription(groupInfoWithMemberCount.getDescription())
                .groupImageUrl(imageUrl)
                .groupMemberCount(groupInfoWithMemberCount.getGroupMemberCount())
                .build();
        }
    }

    @Builder
    public record InviteCode(
        String inviteCode
    ) {

        public static InviteCode from(String inviteCode) {
            return InviteCode.builder()
                .inviteCode(inviteCode)
                .build();
        }
    }

}
