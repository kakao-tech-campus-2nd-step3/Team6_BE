package supernova.whokie.group.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GroupRequest {

    public record Create(
        @NotNull
        String groupName,
        String groupDescription,
        @NotNull
        String groupImageUrl
    ) {

    }

    public record Join(
        @NotNull @Min(1)
        Long groupId
    ) {

    }

    public record Exit(
        @NotNull @Min(1)
        Long groupId
    ) {

    }


    public record Modify(
            @NotNull @Min(1)
            Long groupId,
            @NotNull
            String groupName,
            String description
    ) {

    }
}
