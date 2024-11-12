package supernova.whokie.question.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import supernova.whokie.question.controller.dto.QuestionResponse;
import supernova.whokie.question.service.QuestionService;

@Controller
@RequestMapping("/admin/question")
@RequiredArgsConstructor
public class AdminQuestionViewController {

    private final QuestionService questionService;

    @GetMapping("")
    public String questionList(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<QuestionResponse.Admin> questions = questionService.getAllQuestionPaging(pageable)
                .map(QuestionResponse.Admin::from);
        model.addAttribute("questions", questions);
        return "admin/question/admin_question";
    }


}