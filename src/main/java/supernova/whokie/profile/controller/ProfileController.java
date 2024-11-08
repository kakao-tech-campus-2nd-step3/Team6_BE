package supernova.whokie.profile.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.annotation.VisitorIp;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.profile.controller.dto.ProfileRequest;
import supernova.whokie.profile.controller.dto.ProfileResponse;
import supernova.whokie.profile.service.ProfileService;
import supernova.whokie.profile.service.dto.ProfileModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@Validated
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{user-id}")
    public ProfileResponse.Info getProfileInfo(
            @PathVariable("user-id") @Valid @NotNull @Min(1) Long userId,
            @VisitorIp String visitorIp
    ) {
        ProfileModel.Info response = profileService.getProfile(userId, visitorIp);
        return ProfileResponse.Info.from(response);
    }

    @PatchMapping("bg/upload")
    public GlobalResponse uploadProfileBgImage(
            @Authenticate Long userId,
            @RequestParam("image") @NotNull MultipartFile imageFile
    ) {
        profileService.updateImage(userId, imageFile);
        return GlobalResponse.builder().message("배경 이미지 업로드 성공").build();
    }

    @PatchMapping("modify")
    public GlobalResponse modifyProfile(
            @Authenticate Long userId,
            @RequestBody @Valid ProfileRequest.Modify request
    ) {
        profileService.modifyProfileDescription(userId, request.toCommand());
        return GlobalResponse.builder().message("프로필 설명 수정 완료").build();
    }
}
