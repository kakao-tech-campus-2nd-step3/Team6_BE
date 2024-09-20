package supernova.whokie.group_member.controller;

import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.controller.dto.GroupMemberRequest;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/group")
public class GroupMemberController {

    @PatchMapping("leader")
    public GlobalResponse updateGroupLeader(
            @RequestBody GroupMemberRequest.Modify request
    ) {
        return GlobalResponse.builder().message("OK").build();
    }

    @PostMapping("/expel")
    public GlobalResponse expelGroupMember(
            @RequestBody GroupMemberRequest.Expel request
    ) {
        return GlobalResponse.builder().message("OK").build();
    }

    @GetMapping("/{group-id}/member")
    public GroupMemberResponse.Members getGroupMemberList(
            @PathVariable("group-id") String groupId
    ) {
        return new GroupMemberResponse.Members(List.of(
                new GroupMemberResponse.Member(1L, 1L, GroupRole.MEMBER, "멤버임", LocalDate.now()),
                new GroupMemberResponse.Member(2L, 2L, GroupRole.LEADER, "리터임", LocalDate.now())));
    }
}
