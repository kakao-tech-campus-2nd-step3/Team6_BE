package supernova.whokie.profile_question.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.profile_question.controller.dto.ProfileQuestionResponse;

import supernova.whokie.profile_question.service.ProfileQuestionService;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

@RestController
@RequestMapping("/api/profile/question")
@RequiredArgsConstructor
public class ProfileQuestionController {

    private final ProfileQuestionService profileQuestionService;

    @GetMapping("/{user-id}")
    public PagingResponse<ProfileQuestionResponse.Question> getProfileQuestions(
        @PathVariable("user-id") Long userId,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProfileQuestionModel.Info> page = profileQuestionService.getProfileQuestions(userId,
            pageable);
        return PagingResponse.from(page.map(ProfileQuestionResponse.Question::from));
    }

    @DeleteMapping("/{profile-question-id}")
    public GlobalResponse deleteProfileQuestion(
        @PathVariable("profile-question-id") String profileQuestionId
    ) {
        return GlobalResponse.builder().message("message").build();
    }

}
