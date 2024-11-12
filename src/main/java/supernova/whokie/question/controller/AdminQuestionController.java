package supernova.whokie.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group.constants.GroupConstants;
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
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<QuestionModel.Admin> models = questionService.getAllQuestionPaging(pageable);
        Page<QuestionResponse.Admin> response = models.map(QuestionResponse.Admin::from);
        return PagingResponse.from(response);
    }

    @PostMapping("")
    public GlobalResponse postCommonQuestion(
            @RequestBody @Valid QuestionRequest.CommonCreate request
    ) {
        questionService.createCommonQuestion(GroupConstants.COMMON_GROUPS_ID, request.toCommand());
        return GlobalResponse.builder().message("질문이 등록되었습니다.").build();
    }

    @DeleteMapping("/{question-id}")
    public GlobalResponse deleteCommonQuestion(
            @PathVariable("question-id") Long questionId
    ) {
        questionService.deleteCommonQuestion(GroupConstants.COMMON_GROUPS_ID, questionId );
        return GlobalResponse.builder().message("질문이 삭제되었습니다.").build();
    }
}
