package supernova.whokie.group_member.controller;

import jakarta.validation.Valid;
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
import supernova.whokie.group_member.service.dto.GroupMemberModel;
import supernova.whokie.group_member.service.dto.GroupMemberModel.Members;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @PatchMapping("leader")
    public GlobalResponse updateGroupLeader(
            @Valid @RequestBody GroupMemberRequest.Modify request,
            @Authenticate Long userId
    ) {
        groupMemberService.delegateLeader(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹장 위임에 성공하였습니다.").build();
    }

    @PostMapping("/expel")
    public GlobalResponse expelGroupMember(
            @Valid @RequestBody GroupMemberRequest.Expel request,
            @Authenticate Long userId
    ) {
        groupMemberService.expelMember(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹 멤버 강퇴에 성공하였습니다.").build();
    }

    @GetMapping("/{group-id}/member")
    public GroupMemberResponse.Members getGroupMemberList(
            @PathVariable("group-id") Long groupId,
            @Authenticate Long userId
    ) {
        GroupMemberModel.Members model = groupMemberService.getGroupMembers(userId, groupId);
        return GroupMemberResponse.Members.from(model);
    }
}
