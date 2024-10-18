package supernova.whokie.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserReaderService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

    }

    @Transactional(readOnly = true)
    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public List<Users> getUserListByKakaoIdIn(List<Long> kakaoId) {
        return userRepository.findByKakaoIdIn(kakaoId);
    }

    @Transactional(readOnly = true)
    public List<Users> getUserListByUserIdIn(List<Long> userId) {
        return userRepository.findByIdIn(userId);
    }

}
