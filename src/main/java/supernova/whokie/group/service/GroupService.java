package supernova.whokie.group.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupsRepository;
import supernova.whokie.group.service.dto.GroupCommand;

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

}
