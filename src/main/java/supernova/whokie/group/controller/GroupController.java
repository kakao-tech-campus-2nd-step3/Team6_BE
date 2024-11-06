package supernova.whokie.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
@Validated
public class GroupController {

    private final GroupService groupService;

    @PostMapping("")
    public GroupResponse.Info createGroup(
        @RequestBody @Valid GroupRequest.Create request,
        @Authenticate Long userId
    ) {
        GroupModel.Info model = groupService.createGroup(request.toCommand(), userId);
        return GroupResponse.Info.from(model);
    }

    @GetMapping("/{group-id}/invite")
    public GroupResponse.InviteCode inviteGroup(
        @Authenticate Long userId,
        @PathVariable("group-id") @NotNull @Min(1) Long groupId
    ) {
        GroupModel.InviteCode model = groupService.inviteGroup(userId, groupId);
        return GroupResponse.InviteCode.from(model);

    }

    @GetMapping("/my")
    public PagingResponse<GroupResponse.InfoWithCount> getGroupPaging(
        @Authenticate Long userId,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<GroupModel.InfoWithMemberCount> groupPage = groupService.getGroupPaging(userId,
            pageable);
        Page<GroupResponse.InfoWithCount> groupResponse = groupPage.map(
            GroupResponse.InfoWithCount::from);
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

    @PatchMapping("/modify/image/{group-id}")
    public GlobalResponse modifyGroupImage(
        @Authenticate Long userId,
        @PathVariable("group-id") Long groupId,
        @RequestParam("image") @NotNull MultipartFile imageFile
    ) {
        groupService.modifyGroupImage(userId, groupId, imageFile);
        return GlobalResponse.builder().message("그룹 이미지 업로드 성공").build();
    }

    @GetMapping("/info/{group-id}")
    public GroupResponse.InfoWithCount getGroupInfo(
        @PathVariable("group-id") @NotNull @Min(1) Long groupId
    ) {
        GroupModel.InfoWithMemberCount groupModel = groupService.getGroupInfo(groupId);
        return GroupResponse.InfoWithCount.from(groupModel);
    }
}
