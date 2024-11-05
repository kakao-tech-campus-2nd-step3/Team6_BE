package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class ProfileReaderService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public Profile getByUserId(Long userId) {
        return profileRepository.findByUsersId(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
    }
}
