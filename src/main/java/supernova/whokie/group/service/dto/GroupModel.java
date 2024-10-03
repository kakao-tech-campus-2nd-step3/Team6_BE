package supernova.whokie.group.service.dto;

import lombok.Builder;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount;

public class GroupModel {

    @Builder
    public record Info(
        Long groupId,
        String groupName,
        String description,
        String groupImageUrl
    ) {

        public static Info from(Groups groups) {
            return Info.builder()
                .groupId(groups.getId())
                .groupName(groups.getGroupName())
                .description(groups.getDescription())
                .groupImageUrl(groups.getGroupImageUrl())
                .build();
        }
    }

    @Builder
    public record InfoWithMemberCount(
        Long groupId,
        String groupName,
        String description,
        String groupImageUrl,
        Long groupMemberCount
    ) {

        public static InfoWithMemberCount from(GroupInfoWithMemberCount groupInfoWithMemberCount) {
            return InfoWithMemberCount.builder()
                .groupId(groupInfoWithMemberCount.getGroupId())
                .groupName(groupInfoWithMemberCount.getGroupName())
                .description(groupInfoWithMemberCount.getDescription())
                .groupImageUrl(groupInfoWithMemberCount.getGroupImageUrl())
                .groupMemberCount(groupInfoWithMemberCount.getGroupMemberCount())
                .build();
        }
    }
}
