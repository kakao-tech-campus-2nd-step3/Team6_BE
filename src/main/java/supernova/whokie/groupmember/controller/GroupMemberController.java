package supernova.whokie.groupmember.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.groupmember.controller.dto.GroupMemberRequest;
import supernova.whokie.groupmember.controller.dto.GroupMemberResponse;
import supernova.whokie.groupmember.service.GroupMemberService;
import supernova.whokie.groupmember.service.dto.GroupMemberModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
@Validated
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @PatchMapping("leader")
    public GlobalResponse updateGroupLeader(
        @RequestBody @Valid GroupMemberRequest.Modify request,
        @Authenticate Long userId
    ) {
        groupMemberService.delegateLeader(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹장 위임에 성공하였습니다.").build();
    }

    @PostMapping("/expel")
    public GlobalResponse expelGroupMember(
        @RequestBody @Valid GroupMemberRequest.Expel request,
        @Authenticate Long userId
    ) {
        groupMemberService.expelMember(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹 멤버 강퇴에 성공하였습니다.").build();
    }

    @GetMapping("/{group-id}/member")
    public GroupMemberResponse.Members getGroupMemberList(
        @PathVariable("group-id") @NotNull @Min(1) Long groupId,
        @Authenticate Long userId
    ) {
        GroupMemberModel.Members model = groupMemberService.getGroupMembers(userId, groupId);
        return GroupMemberResponse.Members.from(model);
    }

    @PostMapping("/join")
    public GlobalResponse joinGroup(
        @RequestBody @Valid GroupMemberRequest.Join request,
        @Authenticate Long userId
    ) {
        groupMemberService.joinGroup(request.toCommand(), userId);
        return GlobalResponse.builder().message("그룹 가입에 성공했습니다.").build();
    }

    @PostMapping("/exit")
    public GlobalResponse exitGroup(
        @RequestBody @Valid GroupMemberRequest.Exit request,
        @Authenticate Long userId
    ) {

        groupMemberService.exitGroup(request.toCommand(), userId);
        return GlobalResponse.builder().message("그룹을 탈퇴하였습니다.").build();
    }
}
