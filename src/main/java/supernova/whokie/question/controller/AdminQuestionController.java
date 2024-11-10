package supernova.whokie.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.AdminAuthenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.question.controller.dto.QuestionRequest;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.service.QuestionService;
import supernova.whokie.question.service.dto.QuestionModel;

@RestController
@RequestMapping("/api/admin/question")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @GetMapping("")
    public PagingResponse<QuestionResponse.Admin> getAllQuestionPaging(
            @AdminAuthenticate Long userId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<QuestionModel.Admin> models = questionService.getAllQuestionPaging(pageable);
        Page<QuestionResponse.Admin> response = models.map(QuestionResponse.Admin::from);
        return PagingResponse.from(response);
    }

    @PostMapping("")
    public GlobalResponse postCommonQuestion(
            @AdminAuthenticate Long userId,
            @RequestBody @Valid QuestionRequest.CommonCreate request
    ) {
        questionService.createCommonQuestion(1L, request.toCommand());
        return GlobalResponse.builder().message("질문이 등록되었습니다.").build();
    }

    @DeleteMapping("/{question-id}")
    public GlobalResponse deleteCommonQuestion(
            @AdminAuthenticate Long userId,
            @PathVariable("question-id") Long questionId
    ) {
        questionService.deleteCommonQuestion(1L, questionId );
        return GlobalResponse.builder().message("질문이 삭제되었습니다.").build();
    }
}
