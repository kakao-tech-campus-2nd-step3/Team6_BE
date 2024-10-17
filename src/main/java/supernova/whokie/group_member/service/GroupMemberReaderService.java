package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;

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


}
