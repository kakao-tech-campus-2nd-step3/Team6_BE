package supernova.whokie.profile_answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profile_answer.ProfileAnswer;
import supernova.whokie.profile_answer.infrastructure.repository.ProfileAnswerRepository;

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
    public Page<ProfileAnswer> getAllByUserId(Long userId, Pageable pageable) {
        return profileAnswerRepository.findAllByUserId(userId, pageable);
    }

}
