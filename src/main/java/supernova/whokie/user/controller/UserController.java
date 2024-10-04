package supernova.whokie.user.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.service.UserService;
import supernova.whokie.user.service.dto.UserModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String loginUrl = userService.getCodeUrl();

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header("location", loginUrl)
            .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<GlobalResponse> registerUser(
            @RequestParam("code") @NotNull String code
    ) {
        String token = userService.register(code);

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Authorization", token)
            .body(GlobalResponse.builder().message("로그인이 완료되었습니다.").build());
    }

    @GetMapping("/mypage")
    public ResponseEntity<UserResponse.Info> getUserInfo(
            @Authenticate Long userId
    ) {
        UserModel.Info response = userService.getUserInfo(userId);
        return ResponseEntity.ok().body(UserResponse.Info.from(response));
    }

    @GetMapping("/point")
    public ResponseEntity<UserResponse.Point> getUserPoint(
            @Authenticate Long userId
    ) {
        UserModel.Point response = userService.getPoint(userId);
        return ResponseEntity.ok().body(UserResponse.Point.from(response));
    }

    @GetMapping("/test/login")
    public ResponseEntity<GlobalResponse> testLogin() { // 로그인 테스트용
        String token = userService.testRegister();

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Authorization", token)
            .body(GlobalResponse.builder().message("로그인이 완료되었습니다.").build());
    }
}
