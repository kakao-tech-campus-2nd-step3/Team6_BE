package supernova.whokie.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.question.Question;
import supernova.whokie.question.repository.QuestionRepository;

@Service
@RequiredArgsConstructor
public class QuestionReaderService {

    private final QuestionRepository questionRepository;

    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.QUESTION_NOT_FOUND_MESSAGE));
    }

}
