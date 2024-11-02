package supernova.whokie.friend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.apicaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apicaller.dto.KakaoDto;
import supernova.whokie.friend.service.dto.FriendCommand;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FriendService {

    private final FriendKakaoApiCaller apiCaller;
    private final UserReaderService userReaderService;
    private final FriendReaderService friendReaderService;
    private final KakaoTokenService kakaoTokenService;
    private final FriendWriterService friendWriterService;
    private final S3Service s3Service;

    @Transactional
    public List<FriendModel.Info> getKakaoFriends(Long userId) {
        // userId로 kakaoAccessToken 조회
        String accessToken = kakaoTokenService.refreshIfAccessTokenExpired(userId);
        List<KakaoDto.Profile> profiles = apiCaller.getKakaoFriends(accessToken).elements();
        List<Long> kakaoId = profiles.stream().map(KakaoDto.Profile::id).toList();
        List<Users> friendUsers = userReaderService.getUserListByKakaoIdIn(kakaoId);

        // 사용자의 모든 Friend 조회
        Set<Long> existingSet = friendReaderService.getFriendIdsByHostUser(userId);

        return friendUsers.stream()
            .map(user -> {
                boolean isFriend = existingSet.contains(user.getId());
                String imageUrl = user.getImageUrl();
                if (user.isImageUrlStoredInS3()) {
                    imageUrl = s3Service.getSignedUrl(imageUrl);
                }

                return FriendModel.Info.from(user, isFriend, imageUrl);
            })
            .toList();
    }

    @Transactional
    public void updateFriends(Long userId, FriendCommand.Update command) {
        Users host = userReaderService.getUserById(userId);
        friendWriterService.deleteAllByHostUser(host);
        saveFriends(host, command);
    }

    @Transactional
    public void saveFriends(Users host, FriendCommand.Update command) {
        List<Users> friendUserList = userReaderService.getUserListByUserIdIn(command.friendIds());
        List<Friend> newFriends = command.toEntity(host, friendUserList);
        friendWriterService.saveAll(newFriends);
    }
}
