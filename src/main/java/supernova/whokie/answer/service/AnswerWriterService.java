package supernova.whokie.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.repository.AnswerRepository;


@Service
@RequiredArgsConstructor
public class AnswerWriterService {

    private final AnswerRepository answerRepository;

    @Transactional
    public void save(Answer answer) {
        answerRepository.save(answer);
    }
}
