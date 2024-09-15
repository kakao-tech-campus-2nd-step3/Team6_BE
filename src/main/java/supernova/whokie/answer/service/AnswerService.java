package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;

    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable){
        Page<Answer> answers = answerRepository.findAll(pageable);

        List<AnswerResponse.Record> answerResponse = answers.stream()
                .map(AnswerResponse.Record::fromEntity)
                .toList();

        return PagingResponse.from(new PageImpl<>(answerResponse, pageable, answers.getTotalElements()));
    }

    public void answerToCommonQuestion(Users user, Long questionId, Long pickedId){
        Question question = questionRepository.findById(questionId)
                .orElse(null); //TODO 예외처리
        Users picked = usersRepository.findById(pickedId)
                .orElse(null); //TODO 예외처리

        Answer answer = Answer.builder()
                .question(question)
                .picker(user)
                .picked(picked)
                .hintCount(0)
                .build();
        answerRepository.save(answer);
    }


}
