package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.controller.dto.AnswerRecord;
import supernova.whokie.answer.controller.dto.AnswerResponse;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;

    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable, Users user) {
        Page<Answer> answers = answerRepository.findAllByPicker(pageable, user);

        List<AnswerResponse.Record> answerResponse = answers.stream()
                .map(answer -> {
                    AnswerRecord answerRecord = AnswerRecord.from(answer);
                    return AnswerResponse.Record.from(answerRecord);
                })
                .toList();

        return PagingResponse.from(new PageImpl<>(answerResponse, pageable, answers.getTotalElements()));
    }

    public void answerToCommonQuestion(Users user, Long questionId, Long pickedId){
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 질문을 찾을 수 없습니다."));
        Users picked = usersRepository.findById(pickedId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Answer answer = Answer.builder()
                .question(question)
                .picker(user)
                .picked(picked)
                .hintCount(0)
                .build();
        answerRepository.save(answer);
    }



}
