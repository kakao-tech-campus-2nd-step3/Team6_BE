package supernova.whokie.profile_question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.profile_question.infrastructure.repository.ProfileQuestionRepository;

@Service
@RequiredArgsConstructor
public class ProfileQuestionReaderService {

    private final ProfileQuestionRepository profileQuestionRepository;

    @Transactional(readOnly = true)
    public ProfileQuestion getById(Long id) {
        return profileQuestionRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(MessageConstants.PROFILE_QUESTION_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public ProfileQuestion getByIdWithUser(Long profileQuestionId) {
        return profileQuestionRepository.findByIdWithUser(profileQuestionId).orElseThrow(
            () -> new EntityNotFoundException(MessageConstants.PROFILE_QUESTION_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Page<ProfileQuestion> getAllByUserId(Long userId, Pageable pageable) {
        return profileQuestionRepository.findAllByUserId(userId, pageable);
    }
}
