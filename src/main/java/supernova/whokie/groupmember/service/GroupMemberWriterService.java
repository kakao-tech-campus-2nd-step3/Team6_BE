package supernova.whokie.groupmember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.infrastructure.repository.GroupMemberRepository;

@Service
@RequiredArgsConstructor
public class GroupMemberWriterService {

    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void save(GroupMember groupMember) {
        groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void expelMember(Long memberId, Long groupId) {
        groupMemberRepository.deleteByUserIdAndGroupId(memberId, groupId);
    }

    @Transactional
    public void deleteByUserIdAndGroupId(Long groupId, Long userId) {
        groupMemberRepository.deleteByUserIdAndGroupId(userId, groupId);
    }

}
