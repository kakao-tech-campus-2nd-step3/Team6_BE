package supernova.whokie.friend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.apiCaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apiCaller.dto.KakaoDto;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.friend.service.dto.FriendCommand;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendService {
    private final FriendKakaoApiCaller apiCaller;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public List<FriendModel.Info> getKakaoFriends(Long userId) {
        // userId로 kakaoAccessToken 조회
        String accessToken = "testwsetest";
        List<KakaoDto.Profile> profiles = apiCaller.getKakaoFriends(accessToken).elements();
        List<String> kakaoCodes = profiles.stream().map(KakaoDto.Profile::uuid).toList();
        List<Users> friendUsers = userRepository.findByKakaoCodeIn(kakaoCodes);

        // 사용자의 모든 Friend 조회
        List<Friend> existingList = friendRepository.findByHostUserId(userId);
        Set<Long> existingSet = extractFriendUserIdAsSet(existingList);

        return friendUsers.stream()
                .map(user -> FriendModel.Info.from(user, user.isFriend(existingSet)))
                .toList();
    }

    @Transactional
    public void updateFriends(Long userId, FriendCommand.Update command) {
        // 사용자 Users 조회
        Users host = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 사용자의 모든 Friend 조회
        List<Friend> existingFriends = friendRepository.findByHostUser(host);

        // 친구 삭제
        deleteFriends(command, existingFriends);

        // 친구 저장
        saveFriends(host, command, existingFriends);
    }

    public void saveFriends(Users host, FriendCommand.Update command, List<Friend> existingFriends) {
        // 새로운 Friend 필터링
        List<Long> newFriendIds = filteringNewFriendUserIds(command.friendIds(), existingFriends);

        // 새로운 친구 Users 조회
        List<Users> friendUsers = userRepository.findByIdIn(newFriendIds);

        // 새로운 Friends 저장
        friendRepository.saveAll(command.toEntity(host, friendUsers));
    }

    public void deleteFriends(FriendCommand.Update command, List<Friend> existingFriends) {
        // 삭제할 Friend 필터링
        List<Friend> deleteFriends = filteringDeleteFriendUserIds(command.friendIds(), existingFriends);

        // Friends 삭제
        friendRepository.deleteAll(deleteFriends);
    }

    public List<Long> filteringNewFriendUserIds(List<Long> friendUserIds, List<Friend> existingFriends) {
        List<Long> existingFriendIds = existingFriends.stream().map(Friend::getFriendUserId).toList();
        return friendUserIds.stream()
                .filter(id -> !existingFriendIds.contains(id))
                .toList();
    }

    public List<Friend> filteringDeleteFriendUserIds(List<Long> friendUserIds, List<Friend> existingFriends) {
        return existingFriends.stream()
                .filter(friend -> !friendUserIds.contains(friend.getFriendUserId()))
                .toList();
    }

    public Set<Long> extractFriendUserIdAsSet(List<Friend> friends) {
        return friends.stream().map(Friend::getFriendUserId).collect(Collectors.toSet());
    }
}
