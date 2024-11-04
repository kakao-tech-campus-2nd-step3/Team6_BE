package supernova.whokie.profileanswer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profileanswer.ProfileAnswer;
import supernova.whokie.profileanswer.infrastructure.repository.ProfileAnswerRepository;

@Service
@RequiredArgsConstructor
public class ProfileAnswerReaderService {

    private final ProfileAnswerRepository profileAnswerRepository;

    @Transactional(readOnly = true)
    public ProfileAnswer getByIdWithAnsweredUser(Long id) {
        return profileAnswerRepository.findByIdWithAnsweredUser(id)
            .orElseThrow(() -> new EntityNotFoundException(
                MessageConstants.PROFILE_ANSWER_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Page<ProfileAnswer> getAllByUserIdAndQuestionId(Long userId, Long questionId, Pageable pageable) {
        return profileAnswerRepository.findAllByUserIdAndQuestionId(userId, questionId, pageable);
    }

}
