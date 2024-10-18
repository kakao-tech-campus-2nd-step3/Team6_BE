package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupWriterService {

    private final GroupRepository groupRepository;

    public void save(Groups group) {
        groupRepository.save(group);
    }

}
