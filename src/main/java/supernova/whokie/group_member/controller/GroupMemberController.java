package supernova.whokie.group_member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.controller.dto.GroupMemberRequest;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;

import java.time.LocalDate;
import java.util.List;
import supernova.whokie.group_member.service.GroupMemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @PatchMapping("leader")
    public GlobalResponse updateGroupLeader(
            @RequestBody GroupMemberRequest.Modify request,
            @Authenticate Long userId
    ) {
        groupMemberService.delegateLeader(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹장 위임에 성공하였습니다.").build();
    }

    @PostMapping("/expel")
    public GlobalResponse expelGroupMember(
            @RequestBody GroupMemberRequest.Expel request,
            @Authenticate Long userId
    ) {
        groupMemberService.expelMember(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹 멤버 강퇴에 성공하였습니다.").build();
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
