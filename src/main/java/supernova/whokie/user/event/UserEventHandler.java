package supernova.whokie.user.event;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.service.UserService;

@Component
@AllArgsConstructor
public class UserEventHandler {
    private final S3Service s3Service;
    private final UserService userService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void UserImageUploadListener(UserEventDto.UploadImage event) {
        MultipartFile imageFile = userService.downloadImageFile(event.imageUrl());
        String key = s3Service.createKey(event.folderName(), event.userId(), imageFile, Constants.FILE_TYPE.get(imageFile.getContentType()));
        s3Service.uploadFile(imageFile, key);
        userService.updateImageUrl(event.userId(), key);
    }
}
