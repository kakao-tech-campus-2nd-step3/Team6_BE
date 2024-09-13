package supernova.whokie.friend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.friend.controller.dto.FriendRequest;
import supernova.whokie.global.dto.GlobalResponse;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @PostMapping("")
    public GlobalResponse postFriend(
            @RequestBody FriendRequest.Add request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

}
