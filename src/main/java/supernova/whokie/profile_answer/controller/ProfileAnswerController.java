package supernova.whokie.profile_answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.profile_answer.controller.dto.ProfileAnswerRequest;
import supernova.whokie.profile_answer.controller.dto.ProfileAnswerRequest.Answer;
import supernova.whokie.profile_answer.controller.dto.ProfileAnswerResponse;
import supernova.whokie.profile_answer.service.ProfileAnswerService;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerModel;

@RestController
@RequiredArgsConstructor
public class ProfileAnswerController {

    private final ProfileAnswerService profileAnswerService;

    @GetMapping("/api/profile/answer")
    public PagingResponse<ProfileAnswerResponse.Answer> getProfileAnswerPaging(
        @Authenticate Long userId,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProfileAnswerModel.Info> page = profileAnswerService.getProfileAnswers(userId,
            pageable);
        return PagingResponse.from(page.map(ProfileAnswerResponse.Answer::from));
    }

    @PostMapping("/api/profile/answer")
    public GlobalResponse postProfileAnswer(
        @Authenticate Long userId,
        @RequestBody @Valid ProfileAnswerRequest.Answer request
    ) {
        profileAnswerService.createProfileAnswer(userId, request.toCommand());
        return GlobalResponse.builder().message("저장에 성공했습니다.").build();
    }

    @DeleteMapping("/api/profile/answer/{profile-answer-id}")
    public GlobalResponse deleteProfileAnswer(
        @Authenticate Long userId,
        @PathVariable("profile-answer-id") Long profileAnswerId
    ) {
        profileAnswerService.deleteProfileAnswer(userId, profileAnswerId);
        return GlobalResponse.builder().message("message").build();
    }
}
