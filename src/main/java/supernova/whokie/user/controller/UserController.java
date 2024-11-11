package supernova.whokie.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.annotation.TempUser;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.user.controller.dto.UserRequest;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.service.UserService;
import supernova.whokie.user.service.dto.UserModel;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Validated
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
    public ResponseEntity<UserResponse.Login> registerUser(
            @RequestParam("code") @NotBlank String code
    ) {
        UserModel.Login model = userService.register(code);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", model.jwt())
                .path("/")
                .maxAge(Duration.ofHours(24))
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header("Authorization", model.jwt())
                .body(UserResponse.Login.from(model));
    }

    @PostMapping("/information")
    public ResponseEntity<UserResponse.Login> postPersonalInformation(
            @RequestBody @Valid UserRequest.Info request,
            @TempUser Long userId
    ) {
        UserModel.Login model = userService.addPersonalInformation(userId, request.toCommand());

        return ResponseEntity.ok()
                .header("Authorization", model.jwt())
                .body(UserResponse.Login.from(model));
    }

    @GetMapping("/point")
    public UserResponse.Point getUserPoint(
            @Authenticate Long userId
    ) {
        UserModel.Point response = userService.getPoint(userId);
        return UserResponse.Point.from(response);
    }

    @PatchMapping("/image")
    public GlobalResponse updateUserImage(
            @Authenticate Long userId,
            @RequestParam("image") @NotNull MultipartFile imageFile
    ) {
        userService.uploadImageUrl(userId, imageFile);
        return GlobalResponse.builder().message("프로필 이미지 업데이트 성공").build();
    }
}
