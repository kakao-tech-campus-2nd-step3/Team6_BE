package supernova.whokie.user.event;

import lombok.Builder;

public class UserEventDto {
    @Builder
    public record UploadImage(
            String imageUrl,
            String folderName,
            Long userId
    ) {
        public static UploadImage toDto(String imageUrl, String folderName, Long userId) {
            return UploadImage.builder()
                    .imageUrl(imageUrl)
                    .folderName(folderName)
                    .userId(userId)
                    .build();
        }
    }
}
