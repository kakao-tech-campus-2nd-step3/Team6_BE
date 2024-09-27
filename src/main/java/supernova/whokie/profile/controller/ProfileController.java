package supernova.whokie.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.profile.controller.dto.ProfileResponse;
import supernova.whokie.profile.service.ProfileService;
import supernova.whokie.profile.service.dto.ProfileModel;
import supernova.whokie.profile.service.dto.ProfileModel.Info;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{user-id}")
    public ResponseEntity<ProfileResponse.Info> getProfileInfo(
            @PathVariable("user-id") Long userId
    ) {
        ProfileModel.Info response = profileService.getProfile(userId);
        return ResponseEntity.ok().body(ProfileResponse.Info.from(response));
    }

    @GetMapping("/increase")
    public GlobalResponse increaseVisited() {
        return GlobalResponse.builder().message("message").build();
    }
}
