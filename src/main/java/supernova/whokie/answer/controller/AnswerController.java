package supernova.whokie.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.answer.controller.dto.AnswerRequest;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.service.AnswerService;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;

import java.util.List;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/common")
    public GlobalResponse common(
            @Valid @RequestBody AnswerRequest.Common request,
            @Authenticate Long userId
    ) {
        answerService.answerToCommonQuestion(userId, request.toCommand());
        return GlobalResponse.builder().message("답변 완료").build();
    }

    @PostMapping("/group")
    public GlobalResponse group(
            @RequestBody AnswerRequest.Group request
    ) {
        return GlobalResponse.builder().message("dummy").build();
    }

    @GetMapping("/refresh")
    public AnswerResponse.Refresh refresh(
            @Authenticate Long userId
    ) {
        AnswerModel.Refresh refresh = answerService.refreshAnswerList(userId);
        return AnswerResponse.Refresh.from(refresh);
    }

    @GetMapping("/record")
    public PagingResponse<AnswerResponse.Record> getAnswerRecord(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @Authenticate Long userId
    ) {
        return answerService.getAnswerRecord(pageable, userId);
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
            @Valid @RequestBody AnswerRequest.Purchase request,
            @Authenticate Long userId
    ) {
        answerService.purchaseHint(userId, request.toCommand());
        return GlobalResponse.builder().message("힌트를 성공적으로 구매하였습니다!").build();
    }
}
