package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupReaderService {

    private final GroupRepository groupRepository;

    public Groups getGroupById(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException(
            MessageConstants.GROUP_NOT_FOUND_MESSAGE));
    }

}
