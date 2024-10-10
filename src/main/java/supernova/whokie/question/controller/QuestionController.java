package supernova.whokie.question.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.question.controller.dto.QuestionRequest;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.service.QuestionService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/group/{group-id}/question/random")
    public QuestionResponse.GroupQuestions getGroupQuestionList(
            @PathVariable("group-id") @NotNull @Min(1) Long groupId,
            @Authenticate Long userId
    ) {
        List<QuestionModel.GroupQuestion> groupQuestions = questionService.getGroupQuestions(userId, groupId);
        return QuestionResponse.GroupQuestions.from(groupQuestions);
    }

    @PostMapping("/group/question")
    public GlobalResponse createGroupQuestion(
            @RequestBody @Valid QuestionRequest.Create request,
            @Authenticate Long userId
    ) {
        questionService.createQuestion(userId, request.toCommand());
        return GlobalResponse.builder().message("질문이 성공적으로 생성되었습니다.").build();
    }

    @PatchMapping("/group/question/status")
    public GlobalResponse approveGroupQuestion(
            @RequestBody @Valid QuestionRequest.Approve request,
            @Authenticate Long userId
    ) {
        questionService.approveQuestion(userId, request.toCommand());
        return GlobalResponse.builder().message("그룹 질문 승인에 성공하였습니다.").build();
    }

    @GetMapping("/group/{group-id}/question")
    public PagingResponse<QuestionResponse.Info> getGroupQuestionPaging(
            @Authenticate Long userId,
            @PathVariable("group-id") @NotNull @Min(1) String groupId,
            @RequestParam("status") @NotNull Boolean status,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<QuestionModel.Info> groupQuestionInfoList = questionService.getGroupQuestionPaging(userId, groupId, status, pageable);
        QuestionResponse.Infos result = QuestionResponse.Infos.from(groupQuestionInfoList);
        return PagingResponse.from(result.infos());
    }

    @GetMapping("/common/question/random")
    public QuestionResponse.CommonQuestions getCommonQuestions(
            @Authenticate Long userId
    ) {
        List<QuestionModel.CommonQuestion> commonQuestions = questionService.getCommonQuestion(userId);
        return QuestionResponse.CommonQuestions.from(commonQuestions);
    }

}
