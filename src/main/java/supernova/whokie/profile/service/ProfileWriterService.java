package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.constants.ProfileConstants;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;
import supernova.whokie.user.Users;

@RequiredArgsConstructor
@Service
public class ProfileWriterService {

    private final ProfileRepository profileRepository;

    @Transactional
    public void saveFromKaKao(Users user) {
        Profile profile = Profile.builder()
            .users(user)
            .backgroundImageUrl(ProfileConstants.DEFAULT_PROFILE_BACKGROUND_IMAGE_URL)
            .build();
        profileRepository.save(profile);
    }

}
