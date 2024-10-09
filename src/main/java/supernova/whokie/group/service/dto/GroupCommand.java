package supernova.whokie.group.service.dto;

import lombok.Builder;
import supernova.whokie.group.Groups;

public class GroupCommand {

    @Builder
    public record Create(
        String groupName,
        String groupDescription,
        String groupImageUrl
    ) {

        public Groups toEntity() {
            return Groups.builder()
                .groupName(groupName)
                .description(groupDescription)
                .groupImageUrl(groupImageUrl)
                .build();
        }
    }

}
