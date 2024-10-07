package supernova.whokie.friend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.friend.controller.dto.FriendRequest;
import supernova.whokie.friend.controller.dto.FriendResponse;
import supernova.whokie.friend.service.FriendService;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;

import java.util.List;

@RestController
@RequestMapping("/api/friend")
@AllArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("")
    public GlobalResponse updateFriend(
            @Authenticate Long userId,
            @RequestBody @Valid FriendRequest.Add request
    ) {
        friendService.updateFriends(userId, request.toCommand());
        return GlobalResponse.builder().message("친구 목록 갱신 성공").build();
    }

    @GetMapping("")
    public FriendResponse.Infos getKakaoFriends(
            @Authenticate Long userId
    ) {
        List<FriendModel.Info> infos = friendService.getKakaoFriends(userId);
        return FriendResponse.Infos.from(infos);
    }
}
