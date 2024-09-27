package supernova.whokie.friend.event;

import lombok.Builder;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.service.dto.FriendCommand;

import java.util.List;

public class FriendEventDto {

    @Builder
    public record Update(
        Long hostId,
        FriendCommand.Update command,
        List<Friend> existingFriends
    ) {
        public static FriendEventDto.Update toDto(Long hostId, FriendCommand.Update command, List<Friend> existingFriends) {
            return Update.builder()
                    .hostId(hostId)
                    .command(command)
                    .existingFriends(existingFriends)
                    .build();
        }
    }
}
