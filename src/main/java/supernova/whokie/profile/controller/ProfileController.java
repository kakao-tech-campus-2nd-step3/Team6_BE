package supernova.whokie.profile.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.profile.controller.dto.ProfileResponse;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping("/{user-id}")
    public ProfileResponse.Info getProfileInfo(
        @PathVariable("user-id") Long userId
    ) {
        return ProfileResponse.Info.builder()
            .totalVisited(1)
            .totalVisited(12)
            .description("홍길동의 프로필")
            .backgroundImageUrl("backgroundImageUrl")
            .name("홍길동")
            .build();
    }

    @GetMapping("/increase")
    public GlobalResponse increaseVisited() {
        return GlobalResponse.builder().message("message").build();
    }
}
