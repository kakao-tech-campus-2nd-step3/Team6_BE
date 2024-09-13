package supernova.whokie.friend;

import org.springframework.web.bind.annotation.*;
import supernova.whokie.friend.controller.dto.FriendRequest;
import supernova.whokie.friend.controller.dto.FriendResponse;
import supernova.whokie.global.dto.GlobalResponse;

import java.util.List;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @PostMapping("")
    public GlobalResponse postFriend(
            @RequestBody FriendRequest.Add request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

    @GetMapping("")
    public FriendResponse.Infos getFriends() {
        return FriendResponse.Infos.builder()
                .friends(
                        List.of(
                                FriendResponse.Info.builder().friendId(1L).name("홍길동").imageUrl("홍길동사진").build(),
                                FriendResponse.Info.builder().friendId(2L).name("홍길동2").imageUrl("홍길동사진2").build()
                        )
                )
                .build();
    }
}
