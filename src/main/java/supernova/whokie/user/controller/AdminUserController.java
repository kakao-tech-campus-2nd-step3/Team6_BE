package supernova.whokie.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.annotation.AdminAuthenticate;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.service.UserService;
import supernova.whokie.user.service.dto.UserModel;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("")
    public PagingResponse<UserResponse.Info> getAllUsers(
            @AdminAuthenticate Long userId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<UserModel.Info> models = userService.getAllUsersPaging(pageable);
        Page<UserResponse.Info> response = models.map(UserResponse.Info::from);
        return PagingResponse.from(response);
    }
}
