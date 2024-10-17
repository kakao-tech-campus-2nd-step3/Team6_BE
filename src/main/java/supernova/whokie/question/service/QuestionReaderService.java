package supernova.whokie.question.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.repository.QuestionRepository;

@Service
@RequiredArgsConstructor
public class QuestionReaderService {

    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.QUESTION_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public List<Question> getRandomQuestions(Pageable pageable) {
        return questionRepository.findRandomQuestions(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Question> getAllByStatus(Long groupId, QuestionStatus status, Pageable pageable) {
        return questionRepository.findAllByStatus(groupId, status,
            pageable);
    }

    @Transactional(readOnly = true)
    public List<Question> getRandomGroupQuestions(Long groupId, Pageable pageable) {
        return questionRepository.findRandomGroupQuestions(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public Question getQuestionByIdAndGroupId(Long questionId, Long groupId) {
        return questionRepository.findByIdAndGroupId(questionId, groupId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.QUESTION_NOT_FOUND_MESSAGE));
    }

}
