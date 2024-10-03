package supernova.whokie.group.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group.controller.dto.GroupRequest;
import supernova.whokie.group.controller.dto.GroupResponse;

import supernova.whokie.group.service.GroupService;
import supernova.whokie.group.service.dto.GroupModel;
import supernova.whokie.group_member.service.GroupMemberService;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    private final GroupMemberService groupMemberService;

    @PostMapping("")
    public GlobalResponse createGroup(
        @RequestBody @Valid GroupRequest.Create request,
        @Authenticate Long userId
    ) {
        Long groupId = groupService.createGroup(request.toCommand());
        groupMemberService.addMemberToGroup(request.toGroupMemberCommand(groupId, userId));

        return GlobalResponse.builder().message("그룹 생성에 성공하셨습니다.").build();
    }

    @PostMapping("/join")
    public GlobalResponse joinGroup(
        @RequestBody @Valid GroupRequest.Join request,
        @Authenticate Long userId
    ) {
        groupMemberService.addMemberToGroup(
            request.toGroupMemberCommand(request.groupId(), userId));
        return GlobalResponse.builder().message("그룹 가입에 성공하셨습니다.").build();
    }

    @GetMapping("/{group-id}/invite")
    public String inviteGroup(
        @RequestParam("user-id") Long userId,
        @PathVariable("group-id") Long groupId) {
        return "dummy-url";
    }

    @PostMapping("/exit")
    public GlobalResponse exitGroup(
        @RequestBody @Valid GroupRequest.Exit request,
        @Authenticate Long userId
    ) {
        groupMemberService.removeMemberFromGroup(request.groupId(), userId);
        return GlobalResponse.builder().message("그룹 탈퇴에 성공하였습니다.").build();
    }

    @GetMapping("/{group-id}")
    public PagingResponse<GroupResponse.InfoWithMemberCount> getGroupPaging(
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<GroupModel.InfoWithMemberCount> page = groupService.getGroups(pageable);

        return PagingResponse.from(page.map(GroupResponse.InfoWithMemberCount::from));
    }

    @PatchMapping("modify")
    public GlobalResponse modifyGroup(
        @RequestBody GroupRequest.Modify request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @GetMapping("/{group-id}/info")
    public GroupResponse.Info getGroupInfo(
        @PathVariable("group-id") Long groupId
    ) {
        return null;
    }
}
