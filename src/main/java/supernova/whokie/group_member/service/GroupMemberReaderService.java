package supernova.whokie.group_member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberModel.Members;

@Service
@RequiredArgsConstructor
public class GroupMemberReaderService {

    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public GroupMember getByUserIdAndGroupId(Long userId, Long groupId) {
        return groupMemberRepository.findByUserIdAndGroupId(userId,
                groupId)
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public boolean isGroupMemberExist(Long userId, Long groupId) {
        return groupMemberRepository.existsByUserIdAndGroupId(userId, groupId);
    }

    @Transactional(readOnly = true)
    public Members getGroupMembers(Long userId, Long groupId) {
        if (!groupMemberRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }

        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(groupId);
        return Members.from(groupMembers);
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getRandomGroupMembersByGroupId(Long userId, Long groupId,
        Pageable pageable) {
        return groupMemberRepository.getRandomGroupMember(userId,
            groupId, pageable);
    }

}
