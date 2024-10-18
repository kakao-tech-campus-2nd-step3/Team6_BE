package supernova.whokie.group.service.dto;

import lombok.Builder;
import supernova.whokie.group.repository.dto.GroupInfoWithMemberCount;

public class GroupModel {

    @Builder
    public record InfoWithMemberCount(
            Long groupId,
            String groupName,
            String groupDescription,
            String groupImageUrl,
            Long groupMemberCount
    ) {

        public static InfoWithMemberCount from(GroupInfoWithMemberCount groupInfoWithMemberCount) {
            return InfoWithMemberCount.builder()
                    .groupId(groupInfoWithMemberCount.getGroupId())
                    .groupName(groupInfoWithMemberCount.getGroupName())
                    .groupDescription(groupInfoWithMemberCount.getDescription())
                    .groupImageUrl(groupInfoWithMemberCount.getGroupImageUrl())
                    .groupMemberCount(groupInfoWithMemberCount.getGroupMemberCount())
                    .build();
        }
    }

}
