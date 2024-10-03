package supernova.whokie.group.infrastructure.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupInfoWithMemberCount {

    private Long groupId;
    private String groupName;
    private String description;
    private String groupImageUrl;
    private Long groupMemberCount;
}
