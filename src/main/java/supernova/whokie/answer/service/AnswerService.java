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
import supernova.whokie.user.Users;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private AnswerRepository answerRepository;

    public PagingResponse<AnswerResponse.Record> getAnswerRecord(Pageable pageable, Users user){
        Page<Answer> answers = answerRepository.findAllByPicker(pageable, user);

        List<AnswerResponse.Record> answerResponse = answers.stream()
                .map(AnswerResponse.Record::fromEntity)
                .toList();

        return PagingResponse.from(new PageImpl<>(answerResponse, pageable, answers.getTotalElements()));
    }
}
