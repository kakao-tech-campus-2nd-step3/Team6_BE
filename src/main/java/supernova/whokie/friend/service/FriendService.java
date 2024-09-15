package supernova.whokie.friend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.controller.dto.FriendRequest;
import supernova.whokie.friend.repository.FriendRepository;
import supernova.whokie.friend.service.dto.KakaoDto;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendService {
    private final KakaoApiCaller apiCaller;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public List<Users> getKakaoFriends(String accessToken) {
        List<KakaoDto.Profile> profiles = apiCaller.getKakaoFriends(accessToken).elements();
        // profile에서 kakaoCode 추출
        List<String> kakaoCodes = profiles.stream().map(KakaoDto.Profile::uuid).toList();

        // Users 테이블에서 이미 존재하는 사용자 조회
        List<Users> existingUsers = findExistUsersByKakaoCodes(kakaoCodes);

        // 새로 저장할 Profile 리스트 생성(필터링)
        List<Users> newUsers = filterNewProfiles(profiles, existingUsers);

        // 새로운 User 저장
        List<Users> savedUsers = userRepository.saveAll(newUsers);

        existingUsers.addAll(savedUsers);
        return existingUsers;
    }

    public List<Users> findExistUsersByKakaoCodes(List<String> kakaoCodes) {
        return userRepository.findByKakaoCodeIn(kakaoCodes);
    }

    public List<Users> filterNewProfiles(List<KakaoDto.Profile> profiles, List<Users> users) {
        List<String> existingKakaoCodes = users.stream().map(Users::getKakaoCode).toList();

        return profiles.stream()
                .filter(profile -> !existingKakaoCodes.contains(profile.uuid()))
                .map(profile -> Users.builder()
                        .name(profile.profileNickname())
                        .kakaoCode(profile.uuid())
                        .imageUrl(profile.profileThumbnailImage())
                        .build())
                .toList();
    }

    public void makeFriends(FriendRequest.Add request, Long userId) {
        List<Long> friendIds = request.friends().stream().map(FriendRequest.Id::id).toList();
        List<Users> friendUsers = userRepository.findByIdIn(friendIds);
        Users host = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Friend> existingFriends = friendRepository.findByHostUser(host);
        List<Friend> newFriends = filterNewFriends(friendUsers, existingFriends, host);
        friendRepository.saveAll(newFriends);
    }

    public List<Friend> filterNewFriends(List<Users> friendUsers, List<Friend> friends, Users host) {
        List<Long> existingFriendIds = friends.stream().map(friend -> friend.getFriendUser().getId()).toList();

        return friendUsers.stream()
                .filter(friend -> !existingFriendIds.contains(friend.getId()))
                .map(friend -> Friend.builder().hostUser(host).friendUser(friend).build())
                .toList();
    }
}
