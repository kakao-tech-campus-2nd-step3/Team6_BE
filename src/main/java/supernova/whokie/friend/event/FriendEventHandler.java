package supernova.whokie.friend.event;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import supernova.whokie.friend.service.FriendService;

@Component
@AllArgsConstructor
public class FriendEventHandler {
    private final FriendService friendService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteFriendsListener(FriendEventDto.Update dto) {
        friendService.deleteFriends(dto.command(), dto.existingFriends());
    }


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveFriendsListener(FriendEventDto.Update dto) {
        friendService.saveFriends(dto.hostId(), dto.command(), dto.existingFriends());
    }
}
