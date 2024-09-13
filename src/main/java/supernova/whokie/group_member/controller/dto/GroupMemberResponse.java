package supernova.whokie.group_member.controller.dto;

import supernova.whokie.group_member.GroupRole;

import java.time.LocalDate;
import java.util.List;

public class GroupMemberResponse {

    public record Members(
            List<Member> members
    ) {

    }

    public record Member(
            Long groupMemberId,
            Long userId,
            GroupRole role,
            String userName,
            LocalDate joinedAt
    ) {

    }

    public record Option(
            Long groundMemberId,
            Long userId,
            String userName,
            String imageUrl
    ) {

    }
}
