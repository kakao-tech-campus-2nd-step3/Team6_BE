package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.service.dto.ProfileModel;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileReaderService profileReaderService;

    public ProfileModel.Info getProfile(Long userId) {
        Profile profile = profileReaderService.getByUserId(userId);

        return ProfileModel.Info.from(profile);
    }
}
