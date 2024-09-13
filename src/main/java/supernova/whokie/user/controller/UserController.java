package supernova.whokie.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.controller.dto.UserResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user")
public class UserController {

//    @PostMapping("/login")
//    public String login() {
//
//    }

    //    @PostMapping("/logout")
//    public String logout() {
//
//    }

    @GetMapping("/mypage")
    public UserResponse.Info getUserInfo() {
        return new UserResponse.Info("test@email.com", Gender.M, 20, "name", Role.USER, LocalDate.now());
    }

    @GetMapping("/point")
    public UserResponse.Point getUserPoint() {
        return UserResponse.Point.builder().amount(1000).build();
    }
}
