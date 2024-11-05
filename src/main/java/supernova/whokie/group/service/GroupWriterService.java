package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupWriterService {

    private final GroupRepository groupRepository;

    @Transactional
    public void save(Groups group) {
        groupRepository.save(group);
    }

}
