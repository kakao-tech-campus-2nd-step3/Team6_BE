package supernova.whokie.group.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupsRepository;
import supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupCommand;
import supernova.whokie.group.service.dto.GroupModel;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupsRepository groupsRepository;

    @Transactional
    public Long createGroup(GroupCommand.Create command) {
        Groups group = command.toEntity();
        groupsRepository.save(group);
        return group.getId();
    }

    @Transactional(readOnly = true)
    public Page<GroupModel.InfoWithMemberCount> getGroups(Pageable pageable) {
        Page<GroupInfoWithMemberCount> page = groupsRepository.findGroupsWithMemberCount(pageable);

        return page.map(GroupModel.InfoWithMemberCount::from);
    }
}
