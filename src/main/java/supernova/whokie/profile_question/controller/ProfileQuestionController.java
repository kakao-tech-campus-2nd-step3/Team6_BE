package supernova.whokie.profile_question.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.profile_question.controller.dto.ProfileQuestionResponse;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profile/question")
public class ProfileQuestionController {

    @GetMapping("/{user-id}")
    public ProfileQuestionResponse.Questions getProfileQuestions(
            @PathVariable("user-id") String userId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ProfileQuestionResponse.Questions.builder()
                .questions(
                        List.of(
                                new ProfileQuestionResponse.Question(1L, "quest1", LocalDate.now()),
                                new ProfileQuestionResponse.Question(1L, "quest1", LocalDate.now())))
                .build();
    }

    @DeleteMapping("/{profile-question-id}")
    public GlobalResponse deleteProfileQuestion(
            @PathVariable("profile-question-id") String profileQuestionId
    ) {
        return GlobalResponse.builder().message("message").build();
    }

}
