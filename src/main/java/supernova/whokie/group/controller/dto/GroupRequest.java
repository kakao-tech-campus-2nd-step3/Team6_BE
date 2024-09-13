package supernova.whokie.group.controller.dto;

public class GroupRequest {

    public record Create(
        String groupName,
        String groupDescription,
        String groupImageUrl
    ) {

    }

    public record Join(
        Long groupId
    ) {

    }

    public record Exit(
        Long groupId
    ) {

    }


    public record Modify(
            Long groupId,
            String groupName,
            String description
    ) {

    }
}
