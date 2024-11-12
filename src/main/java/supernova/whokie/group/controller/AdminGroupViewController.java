package supernova.whokie.group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import supernova.whokie.group.controller.dto.GroupResponse;
import supernova.whokie.group.service.GroupService;

@Controller
@RequestMapping("/admin/group")
@RequiredArgsConstructor
public class AdminGroupViewController {

    private final GroupService groupService;

    @GetMapping("")
    public String groupList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<GroupResponse.Info> groups;
        if (StringUtils.hasText(keyword)) {
            // 검색어가 있는 경우
            groups = groupService.searchGroups(keyword, pageable)
                    .map(GroupResponse.Info::from);
        } else {
            // 검색어가 없는 경우 전체 목록
            groups = groupService.getAllGroupPaging(pageable)
                    .map(GroupResponse.Info::from);
        }

        model.addAttribute("groups", groups);
        model.addAttribute("keyword", keyword);
        return "admin/group/admin_group";
    }
}