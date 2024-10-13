package supernova.whokie.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group.controller.dto.GroupRequest;
import supernova.whokie.group.controller.dto.GroupResponse;
import supernova.whokie.group.service.GroupService;
import supernova.whokie.group.service.dto.GroupModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    @PostMapping("")
    public GlobalResponse createGroup(
            @RequestBody @Valid GroupRequest.Create request,
            @Authenticate Long userId
    ) {
        groupService.createGroup(request.toCommand(), userId);
        return GlobalResponse.builder().message("그룹이 성공적으로 만들어졌습니다.").build();
    }

    @GetMapping("/{group-id}/invite")
    public String inviteGroup(
            @RequestParam("user-id") @NotNull @Min(1) Long userId,
            @PathVariable("group-id") @NotNull @Min(1) Long groupId
    ) {
        return "dummy-url";
    }

    @GetMapping("/my")
    public PagingResponse<GroupResponse.Info> getGroupPaging(
            @Authenticate Long userId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<GroupModel.InfoWithMemberCount> groupPage = groupService.getGroupPaging(userId,
                pageable);
        Page<GroupResponse.Info> groupResponse = groupPage.map(GroupResponse.Info::from);
        return PagingResponse.from(groupResponse);
    }

    @PatchMapping("/modify")
    public GlobalResponse modifyGroup(
            @Authenticate Long userId,
            @RequestBody @Valid GroupRequest.Modify request
    ) {
        groupService.modifyGroup(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹 정보를 성공적으로 변경했습니다.").build();
    }

    @GetMapping("/info/{group-id}")
    public GroupResponse.Info getGroupInfo(
            @PathVariable("group-id") @NotNull @Min(1) Long groupId
    ) {
        GroupModel.InfoWithMemberCount groupModel = groupService.getGroupInfo(groupId);
        return GroupResponse.Info.from(groupModel);
    }
}
