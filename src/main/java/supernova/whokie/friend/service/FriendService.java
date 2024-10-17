package supernova.whokie.friend.service;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.event.FriendEventDto;
import supernova.whokie.friend.infrastructure.apiCaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apiCaller.dto.KakaoDto;
import supernova.whokie.friend.service.dto.FriendCommand;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

@Service
@AllArgsConstructor
public class FriendService {

    private final FriendKakaoApiCaller apiCaller;
    private final UserReaderService userReaderService;
    private final FriendReaderService friendReaderService;
    private final ApplicationEventPublisher eventPublisher;
    private final KakaoTokenService kakaoTokenService;
    private final FriendWriterService friendWriterService;

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
            .map(user -> FriendModel.Info.from(user, existingSet.contains(user.getId())))
            .toList();
    }

    @Transactional(readOnly = true)
    public void updateFriends(Long userId, FriendCommand.Update command) {
        // 사용자의 모든 Friend 조회
        List<Friend> existingFriends = friendReaderService.findByHostUserIdFetchJoin(userId);

        // 비동기로 친구 삭제 및 저장
        eventPublisher.publishEvent(FriendEventDto.Update.toDto(userId, command, existingFriends));
    }

    @Transactional
    public void saveFriends(Long hostId, FriendCommand.Update command,
        List<Friend> existingFriends) {
        Users host = userReaderService.getUserById(hostId);
        // 새로운 Friend 필터링
        List<Long> newFriendIds = filteringNewFriendUserIds(command.friendIds(), existingFriends);

        // 새로운 친구 Users 조회
        List<Users> friendUsers = userReaderService.getUserListByUserIdIn(newFriendIds);

        List<Friend> newFriends = command.toEntity(host, friendUsers);
        // 새로운 Friends 저장
        friendWriterService.saveAll(newFriends);
    }

    @Transactional
    public void deleteFriends(FriendCommand.Update command, List<Friend> existingFriends) {
        // 삭제할 Friend 필터링
        List<Long> deleteFriendIds = filteringDeleteFriendUserIds(command.friendIds(), existingFriends);

        // Friends 삭제
        friendWriterService.deleteAllById(deleteFriendIds);
    }

    public List<Long> filteringNewFriendUserIds(List<Long> friendUserIds,
        List<Friend> existingFriends) {
        List<Long> existingFriendIds = existingFriends.stream().map(Friend::getFriendUserId)
            .toList();
        return friendUserIds.stream()
            .filter(id -> !existingFriendIds.contains(id))
            .toList();
    }

    public List<Long> filteringDeleteFriendUserIds(List<Long> friendUserIds,
        List<Friend> existingFriends) {
        return existingFriends.stream()
            .filter(friend -> !friendUserIds.contains(friend.getFriendUserId()))
            .map(Friend::getId)
            .toList();
    }

}
