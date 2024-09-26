package supernova.whokie.question.controller;

import lombok.RequiredArgsConstructor;
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

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/group/{group-id}/question/random")
    public QuestionResponse.GroupQuestions getGroupQuestionList
            (@PathVariable("group-id") String groupId
            ) {
        return new QuestionResponse.GroupQuestions(
                List.of(new QuestionResponse.GroupQuestion(1L, "1번질문", List.of(new GroupMemberResponse.Option(1L, 1L, "user1", "imageUrl"), new GroupMemberResponse.Option(1L, 1L, "user1", "imageUrl"))),
                        new QuestionResponse.GroupQuestion(1L, "1번질문", List.of(new GroupMemberResponse.Option(1L, 1L, "user1", "imageUrl"), new GroupMemberResponse.Option(1L, 1L, "user1", "imageUrl"))))
        );
    }

    @PostMapping("/group/question")
    public GlobalResponse createGroupQuestion(
            @RequestBody QuestionRequest.Create request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

    @PatchMapping("/group/question/status")
    public GlobalResponse approveGroupQuestion(
            @RequestBody QuestionRequest.Approve request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

    @GetMapping("/group/{group-id}/question")
    public PagingResponse<QuestionResponse.Info> getGroupQuestionPaging(
            @PathVariable("group-id") String groupId,
            @RequestParam("status") Boolean status,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new PagingResponse<>(
                List.of(new QuestionResponse.Info(1L, "질문1", 1L, true, "작성자1", LocalDate.now()),
                        new QuestionResponse.Info(2L, "질문2", 1L, true, "작성자2", LocalDate.now())),
                0, 10, 1, 1);
    }

    @GetMapping("/common/question/random")
    public QuestionResponse.CommonQuestions getCommonQuestions(
            @Authenticate Long userId
    ) {
        List<QuestionModel.CommonQuestion> commonQuestions = questionService.getCommonQuestion(userId);
        return QuestionResponse.CommonQuestions.from(commonQuestions);
    }

}
