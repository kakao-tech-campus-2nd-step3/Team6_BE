package supernova.whokie.friend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.controller.dto.FriendRequest;
import supernova.whokie.friend.infrastructure.apiCaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apiCaller.dto.KakaoDto;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendService {
    private final FriendKakaoApiCaller apiCaller;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public List<Users> getKakaoFriends(String accessToken) {
        List<KakaoDto.Profile> profiles = apiCaller.getKakaoFriends(accessToken).elements();
        List<String> kakaoCodes = profiles.stream().map(KakaoDto.Profile::uuid).toList();
        return userRepository.findByKakaoCodeIn(kakaoCodes);
    }

    @Transactional
    public void makeFriends(FriendRequest.Add request, Long userId) {
        List<Long> friendIds = request.friends().stream().map(FriendRequest.Id::id).toList();

        // friendIds로 모든 친구 Users 조회
        List<Users> friendUsers = userRepository.findByIdIn(friendIds);

        // 사용자 Users 조회
        Users host = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 사용자 Users의 모든 Friend 조회
        List<Friend> existingFriends = friendRepository.findByHostUser(host);

        // 새로운 Friend 필터링
        List<Friend> newFriends = filterNewFriends(friendUsers, existingFriends, host);

        // 새로운 Friend 저장
        friendRepository.saveAll(newFriends);
    }

    public List<Friend> filterNewFriends(List<Users> friendUsers, List<Friend> friends, Users host) {
        List<Long> existingFriendIds = friends.stream().map(friend -> friend.getFriendUser().getId()).toList();

        return friendUsers.stream()
                .filter(friend -> !existingFriendIds.contains(friend.getId()))
                .map(friend -> Friend.builder()
                        .hostUser(host)
                        .friendUser(friend)
                        .build())
                .toList();
    }
}
