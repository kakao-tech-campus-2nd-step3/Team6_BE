package supernova.whokie.group_member.service.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;

public class GroupMemberModel {

    @Builder
    public record Member(
        Long groupMemberId,
        Long userId,
        String userName,
        LocalDate joinedAt,
        GroupRole role
    ) {

        public static Member from(GroupMember groupMember) {
            return Member.builder()
                .groupMemberId(groupMember.getId())
                .userId(groupMember.getUser().getId())
                .userName(groupMember.getUser().getName())
                .joinedAt(groupMember.getCreatedAt().toLocalDate())
                .role(groupMember.getGroupRole())
                .build();
        }
    }

    @Builder
    public record Members(
        List<Member> members
    ) {

        public static Members from(List<GroupMember> memberList) {
            return Members.builder()
                .members(
                    memberList.stream()
                    .map(Member::from)
                    .toList()
                )
                .build();
        }
    }
}
