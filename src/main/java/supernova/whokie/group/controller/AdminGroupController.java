package supernova.whokie.group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group.controller.dto.GroupResponse;
import supernova.whokie.group.service.GroupService;
import supernova.whokie.group.service.dto.GroupModel;

@RestController
@RequestMapping("/api/admin/group")
@RequiredArgsConstructor
public class AdminGroupController {

    private final GroupService groupService;

    @GetMapping("")
    public PagingResponse<GroupResponse.Info> getAllGroups(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<GroupModel.Info> models = groupService.getAllGroupPaging(pageable);
        Page<GroupResponse.Info> response = models.map(GroupResponse.Info::from);
        return PagingResponse.from(response);
    }

}
