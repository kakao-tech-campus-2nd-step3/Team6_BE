package supernova.whokie.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
        @RequestBody @Valid GroupRequest.Modify request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @GetMapping("/{group-id}/info")
    public GroupResponse.Info getGroupInfo(
        @PathVariable("group-id") @NotNull @Min(1) Long groupId
    ) {
        return new GroupResponse.Info(groupId, "group1", "groupImageUrl", 12);
    }
}
