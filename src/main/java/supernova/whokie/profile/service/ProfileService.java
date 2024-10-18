package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.service.dto.ProfileModel;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.service.RedisVisitService;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileReaderService profileReaderService;
    private final RedisVisitService redisVisitService;

    public ProfileModel.Info getProfile(Long userId, String visitorIp) {
        Profile profile = profileReaderService.getByUserId(userId);
        RedisVisitCount visitCount = redisVisitService.visitProfile(userId, visitorIp);
        return ProfileModel.Info.from(profile, visitCount);
    }
}
