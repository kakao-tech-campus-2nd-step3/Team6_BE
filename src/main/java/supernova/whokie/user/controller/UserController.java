package supernova.whokie.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.controller.dto.UserResponse;

import java.time.LocalDate;
import supernova.whokie.user.service.UserService;

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
    public void registerUser(@RequestParam("code") String code) {
        userService.register(code);
    }

    @GetMapping("/mypage")
    public UserResponse.Info getUserInfo() {
        return new UserResponse.Info("test@email.com", Gender.M, 20, "name", Role.USER, LocalDate.now());
    }

    @GetMapping("/point")
    public UserResponse.Point getUserPoint() {
        return UserResponse.Point.builder().amount(1000).build();
    }
}
