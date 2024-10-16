package supernova.whokie.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserReaderService {

    private final UserRepository userRepository;

    @Transactional
    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

    }

}
