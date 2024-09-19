package supernova.whokie.answer.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.answer.controller.dto.AnswerRequest;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.user.controller.dto.UserResponse;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    @PostMapping("/common")
    public GlobalResponse common(
        @RequestBody AnswerRequest.Common request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @PostMapping("/group")
    public GlobalResponse group(
        @RequestBody AnswerRequest.Group request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @GetMapping("/refresh")
    public AnswerResponse.Refresh refresh() {
        return AnswerResponse.Refresh.builder().users(
                listOf(new UserResponse.PickedInfo(1L, "dummy", "dummy"),
                        new UserResponse.PickedInfo(2L, "dummy", "dummy"),
                        new UserResponse.PickedInfo(3L, "dummy", "dummy"))
        ).build();
    }

    @GetMapping("/record")
    public PagingResponse<Record> getAnswerRecord(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new PagingResponse<>(
                List.of(new AnswerResponse.Record(1L, 2L, "quest1", 1, LocalDate.now()),
                        new AnswerResponse.Record(2L, 2L, "quest1", 2, LocalDate.now())),
                2, 1, 1, 0
        );
    }

    @GetMapping("/hint/{answer-id}")
    public AnswerResponse.Hints getHints(
            @PathVariable("answer-id") String answerId
    ) {
        return AnswerResponse.Hints.builder()
                .hints(
                        List.of(
                                new AnswerResponse.Hint(1, true, "F"),
                                new AnswerResponse.Hint(1, true, "22"),
                                new AnswerResponse.Hint(1, true, null)
                        )
                ).build();
    }

    @PostMapping("/hint")
    public GlobalResponse purchaseHint(
            @RequestBody AnswerRequest.Purchase request
    ) {
        return GlobalResponse.builder().message("message").build();
    }
}
