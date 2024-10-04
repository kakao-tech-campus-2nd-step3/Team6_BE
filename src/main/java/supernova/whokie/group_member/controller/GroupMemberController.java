package supernova.whokie.group_member.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
            @RequestBody @Valid GroupMemberRequest.Modify request
    ) {
        return GlobalResponse.builder().message("OK").build();
    }

    @PostMapping("/expel")
    public GlobalResponse expelGroupMember(
            @RequestBody @Valid GroupMemberRequest.Expel request
    ) {
        return GlobalResponse.builder().message("OK").build();
    }

    @GetMapping("/{group-id}/member")
    public GroupMemberResponse.Members getGroupMemberList(
            @PathVariable("group-id") @NotNull @Min(1) String groupId
    ) {
        return new GroupMemberResponse.Members(List.of(
                new GroupMemberResponse.Member(1L, 1L, GroupRole.MEMBER, "멤버임", LocalDate.now()),
                new GroupMemberResponse.Member(2L, 2L, GroupRole.LEADER, "리터임", LocalDate.now())));
    }
}
