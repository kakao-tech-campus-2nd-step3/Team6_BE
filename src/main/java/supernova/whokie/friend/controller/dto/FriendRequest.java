package supernova.whokie.friend.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class FriendRequest {

    public record Add(
            @NotNull
            @Size(min = 1, message = "Friend Ids cannot be empty")
            List<Id> friends
    ) {

    }

    public record Id(
            @Min(1)
            Long id
    ) {

    }
}
