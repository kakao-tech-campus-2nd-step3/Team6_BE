package supernova.whokie.profile_question.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.profile_question.controller.dto.ProfileQuestionRequest;
import supernova.whokie.profile_question.controller.dto.ProfileQuestionResponse;

import supernova.whokie.profile_question.service.ProfileQuestionService;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

@RestController
@RequiredArgsConstructor
public class ProfileQuestionController {

    private final ProfileQuestionService profileQuestionService;

    @GetMapping("/api/profile/question/{user-id}")
    public PagingResponse<ProfileQuestionResponse.Question> getProfileQuestions(
        @PathVariable("user-id") @NotNull @Min(1) Long userId,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProfileQuestionModel.Info> page = profileQuestionService.getProfileQuestions(userId,
            pageable);
        return PagingResponse.from(page.map(ProfileQuestionResponse.Question::from));
    }

    @DeleteMapping("/api/profile/question/{profile-question-id}")
    public GlobalResponse deleteProfileQuestion(
        @Authenticate Long userId,
        @PathVariable("profile-question-id") @NotNull @Min(1) Long profileQuestionId
    ) {
        profileQuestionService.deleteProfileQuestion(userId, profileQuestionId);
        return GlobalResponse.builder().message("삭제가 완료되었습니다.").build();
    }

    @PostMapping("/api/profile/question")
    public GlobalResponse createProfileQuestion(
        @Authenticate Long userId,
        @RequestBody @Valid ProfileQuestionRequest.Create request
    ) {
        profileQuestionService.createProfileQuestion(userId, request.toCommand());
        return GlobalResponse.builder().message("질문이 등록되었습니다.").build();
    }

}
