package supernova.whokie.group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group.controller.dto.GroupRequest;
import supernova.whokie.group.controller.dto.GroupResponse;

import java.awt.print.Pageable;
import java.util.List;
import supernova.whokie.group.service.GroupService;
import supernova.whokie.group_member.service.GroupMemberService;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    private final GroupMemberService groupMemberService;

    @PostMapping("")
    public GlobalResponse createGroup(
        @RequestBody GroupRequest.Create request,
        @Authenticate Long userId
    ) {
        Long groupId = groupService.createGroup(request.toCommand());
        groupMemberService.addMemberToGroup(request.toGroupMemberCommand(groupId, userId));

        return GlobalResponse.builder().message("그룹 생성에 성공하셨습니다.").build();
    }

    @PostMapping("/join")
    public GlobalResponse joinGroup(
        @RequestBody GroupRequest.Join request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @GetMapping("/{group-id}/invite")
    public String inviteGroup(
        @RequestParam("user-id") Long userId,
        @PathVariable("group-id") Long groupId) {
        return "dummy-url";
    }

    @PostMapping("/exit")
    public GlobalResponse exitGroup(
        @RequestBody GroupRequest.Exit request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @GetMapping("/{group-id}")
    public PagingResponse<GroupResponse.Info> getGroupPaging(
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new PagingResponse<>(
            List.of(new GroupResponse.Info(1L, "group1", "groupImageUrl", 12),
                new GroupResponse.Info(2L, "group2", "groupImageUrl", 12)),
            2, 0, 1, 1);
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
        return new GroupResponse.Info(groupId, "group1", "groupImageUrl", 12);
    }
}
