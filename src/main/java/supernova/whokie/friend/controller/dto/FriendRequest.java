package supernova.whokie.friend.controller.dto;

import java.util.List;

public class FriendRequest {

    public record Add(
            List<Id> friends
    ) {

    }

    public record Id(
            Long id
    ) {

    }
}
