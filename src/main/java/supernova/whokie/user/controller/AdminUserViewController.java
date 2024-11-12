package supernova.whokie.user.controller;

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
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.service.UserService;

@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserViewController {

    private final UserService userService;

    @GetMapping("")
    public String userList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<UserResponse.Info> users;
        if (StringUtils.hasText(keyword)) {
            // 검색어가 있는 경우
            users = userService.searchUsers(keyword, pageable).map(UserResponse.Info::from);
        } else {
            // 전체 목록
            users = userService.getAllUsersPaging(pageable).map(UserResponse.Info::from);
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/user/admin_user";
    }
}