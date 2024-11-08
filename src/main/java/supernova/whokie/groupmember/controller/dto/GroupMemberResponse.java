package supernova.whokie.groupmember.controller.dto;

import java.time.LocalDate;
import lombok.Builder;
import supernova.whokie.groupmember.GroupRole;
import supernova.whokie.groupmember.service.dto.GroupMemberModel;

public class GroupMemberResponse {

    @Builder
    public record Member(
        Long groupMemberId,
        Long userId,
        GroupRole role,
        String userName,
        String memberImageUrl,
        LocalDate joinedAt
    ) {

        public static Member from(GroupMemberModel.Member model) {
            return Member.builder()
                .groupMemberId(model.groupMemberId())
                .userId(model.userId())
                .role(model.role())
                .userName(model.userName())
                .memberImageUrl(model.memberImageUrl())
                .joinedAt(model.joinedAt())
                .build();
        }
    }

    @Builder
    public record Option(
        Long groupMemberId,
        Long userId,
        String userName,
        String imageUrl
    ) {

        public static GroupMemberResponse.Option from(GroupMemberModel.Option groupMember) {
            return GroupMemberResponse.Option.builder()
                .groupMemberId(groupMember.groupMemberId())
                .userId(groupMember.userId())
                .userName(groupMember.userName())
                .imageUrl(groupMember.imageUrl())
                .build();
        }
    }

    @Builder
    public record Role(
        GroupRole role
    ) {

        public static GroupMemberResponse.Role from(GroupMemberModel.Role model) {
            return GroupMemberResponse.Role.builder()
                .role(model.role())
                .build();
        }
    }
}
