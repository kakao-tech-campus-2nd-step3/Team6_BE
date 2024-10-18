package supernova.whokie.group.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import supernova.whokie.group.service.dto.GroupCommand;

public class GroupRequest {

    public record Create(
            @NotBlank
            String groupName,
            @NotBlank
            String groupDescription,
            @NotBlank
            String groupImageUrl
    ) {

        public GroupCommand.Create toCommand() {
            return GroupCommand.Create.builder()
                    .groupName(groupName)
                    .groupDescription(groupDescription)
                    .groupImageUrl(groupImageUrl)
                    .build();
        }
    }

    public record Modify(
            @NotNull @Min(1)
            Long groupId,
            @NotBlank
            String groupName,
            @NotBlank
            String description
    ) {

        public GroupCommand.Modify toCommand() {
            return GroupCommand.Modify.builder()
                    .groupId(groupId)
                    .groupName(groupName)
                    .description(description)
                    .build();
        }

    }
}
