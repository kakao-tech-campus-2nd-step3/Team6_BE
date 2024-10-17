package supernova.whokie.answer.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Users;

@Service
@RequiredArgsConstructor
public class AnswerReaderService {

    private final AnswerRepository answerRepository;

    public Page<Answer> getAnswerList(Pageable pageable, Users user, LocalDateTime startDate,
        LocalDateTime endDate) {
        return answerRepository.findAllByPickerAndCreatedAtBetween(pageable, user, startDate,
            endDate);
    }

    public Answer getAnswerById(Long answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new EntityNotFoundException(
            MessageConstants.ANSWER_NOT_FOUND_MESSAGE));
    }
}
