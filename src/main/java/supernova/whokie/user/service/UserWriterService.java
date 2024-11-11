package supernova.whokie.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.apicaller.dto.KakaoAccount;
import supernova.whokie.user.infrastructure.apicaller.dto.UserInfoResponse;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserCommand;

@Service
@RequiredArgsConstructor
public class UserWriterService {

    private final UserRepository userRepository;

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void save(Users user) {
        userRepository.save(user);
    }

    @Transactional
    public Users saveUserFromKakao(UserInfoResponse userInfoResponse) {
        KakaoAccount kakaoAccount = userInfoResponse.kakaoAccount();
        Users user = userRepository.save(
            Users.builder()
//                .name(kakaoAccount.name())
                .email(kakaoAccount.email())
                .point(0)
//                .age(LocalDate.now().getYear() - Integer.parseInt(kakaoAccount.birthYear()))
//                .gender(Gender.fromString(kakaoAccount.gender()))
                .imageUrl(kakaoAccount.profile().profileImageUrl())
                .role(Role.TEMP)
                .kakaoId(userInfoResponse.id())
                .build());
        return user;
    }

    @Transactional
    public Users updateUserPersonalInfo(Long userId, UserCommand.Info command) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        users.updatePersonalInfo(command.name(), command.gender(), command.birthDate());
        return users;
    }
}
