package supernova.whokie.profile_answer.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.profile_answer.controller.dto.ProfileAnswerRequest;
import supernova.whokie.profile_answer.controller.dto.ProfileAnswerResponse;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profile/answer")
public class ProfileAnswerController {

    @GetMapping("/")
    public PagingResponse<ProfileAnswerResponse.Answer> getProfileAnswerPaging(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new PagingResponse<>(
                List.of(new ProfileAnswerResponse.Answer(1L, "answer1", "quest1", LocalDate.now()),
                        new ProfileAnswerResponse.Answer(2L, "answer2", "quest1", LocalDate.now())),
                2, 1, 2, 1);
    }

    @PostMapping("/")
    public GlobalResponse postProfileAnswer(
            @RequestBody ProfileAnswerRequest.Answer request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

    @DeleteMapping("/{profile-answer-id}")
    public GlobalResponse deleteProfileAnswer(
            @PathVariable("profile-answer-id") String profileAnswerId
    ) {
        return GlobalResponse.builder().message("message").build();
    }
}
