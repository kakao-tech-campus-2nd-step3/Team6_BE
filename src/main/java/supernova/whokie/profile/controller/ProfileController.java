package supernova.whokie.profile.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.annotation.VisitorIp;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.profile.controller.dto.ProfileResponse;
import supernova.whokie.profile.service.ProfileService;
import supernova.whokie.profile.service.dto.ProfileModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{user-id}")
    public ResponseEntity<ProfileResponse.Info> getProfileInfo(
            @PathVariable("user-id") @NotNull @Min(1) Long userId,
            @VisitorIp String visitorIp
    ) {
        System.out.println(visitorIp);
        ProfileModel.Info response = profileService.getProfile(userId);
        return ResponseEntity.ok().body(ProfileResponse.Info.from(response));
    }

    @GetMapping("/visited")
    public GlobalResponse increaseVisited() {
        return GlobalResponse.builder().message("message").build();
    }
}
