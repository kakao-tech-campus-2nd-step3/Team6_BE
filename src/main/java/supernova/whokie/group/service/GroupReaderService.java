package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupModel.InfoWithMemberCount;

@Service
@RequiredArgsConstructor
public class GroupReaderService {

    private final GroupRepository groupRepository;

    public Groups getGroupById(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException(
            MessageConstants.GROUP_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Page<InfoWithMemberCount> getGroupPaging(Long userId, Pageable pageable) {
        Page<GroupInfoWithMemberCount> groupPage = groupRepository.findGroupsWithMemberCountByUserId(
            userId,
            pageable);
        return groupPage.map(InfoWithMemberCount::from);
    }

    @Transactional(readOnly = true)
    public GroupInfoWithMemberCount getGroupInfoWithMemberCountByGroupId(Long groupId) {
        return groupRepository.findGroupInfoWithMemberCountByGroupId(groupId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.GROUP_NOT_FOUND_MESSAGE));
    }

}
