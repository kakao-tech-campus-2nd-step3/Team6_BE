package supernova.whokie.groupmember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.infrastructure.repository.GroupMemberRepository;

import java.util.List;

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
    public Page<GroupMember> getGroupMembers(Pageable pageable, Long userId, Long groupId) {
        if (!groupMemberRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }

        return groupMemberRepository.findAllByGroupId(pageable, groupId);
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getRandomGroupMembersByGroupId(Long userId, Long groupId,
        Pageable pageable) {
        return groupMemberRepository.getRandomGroupMemberJoinFetch(userId,
            groupId, pageable);
    }

    @Transactional(readOnly = true)
    public Long groupMemberCountByGroupId(Long groupId) {
        return groupMemberRepository.countByGroupId(groupId);
    }
}
