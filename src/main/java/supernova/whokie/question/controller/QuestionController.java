package supernova.whokie.question.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;
import supernova.whokie.question.controller.dto.QuestionRequest;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.user.controller.dto.UserResponse;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QuestionController {

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
    public QuestionResponse.CommonQuestions getCommonQuestions() {
        return QuestionResponse.CommonQuestions.builder()
                .questions(
                        List.of(new QuestionResponse.CommonQuestion(1L, "질문1", List.of(new UserResponse.PickedInfo(1L, "user1", "imageUrl"), new UserResponse.PickedInfo(2L, "user2", "imageUrl"))),
                                new QuestionResponse.CommonQuestion(2L, "질문2", List.of(new UserResponse.PickedInfo(1L, "user1", "imageUrl"), new UserResponse.PickedInfo(2L, "user2", "imageUrl")))))
                .build();
    }

}
