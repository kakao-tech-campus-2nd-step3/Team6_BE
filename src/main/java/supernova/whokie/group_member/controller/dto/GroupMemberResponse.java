package supernova.whokie.group_member.controller.dto;

import lombok.Builder;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;

import java.time.LocalDate;
import java.util.List;
import supernova.whokie.group_member.service.dto.GroupMemberModel;

public class GroupMemberResponse {

    @Builder
    public record Members(
        List<Member> members
    ) {

        public static GroupMemberResponse.Members from(GroupMemberModel.Members model) {
            return Members.builder()
                .members(
                    model.members().stream()
                        .map(GroupMemberResponse.Member::from)
                        .toList()
                )
                .build();
        }
    }

    @Builder
    public record Member(
            Long groupMemberId,
            Long userId,
            GroupRole role,
            String userName,
            LocalDate joinedAt
    ) {

        public static Member from(GroupMemberModel.Member model) {
            return Member.builder()
                .groupMemberId(model.groupMemberId())
                .userId(model.userId())
                .role(model.role())
                .userName(model.userName())
                .joinedAt(model.joinedAt())
                .build();
        }
    }

    public record Option(
            Long groundMemberId,
            Long userId,
            String userName,
            String imageUrl
    ) {

    }
}
