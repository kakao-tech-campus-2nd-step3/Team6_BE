package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void delegateLeader(Long userId, GroupMemberCommand.Modify command) {
        validateCurrentLeader(userId, command.pastLeaderId());

        GroupMember leader = groupMemberRepository.findByUserIdAndGroupId(command.pastLeaderId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        validateLeader(leader);

        GroupMember newLeader = groupMemberRepository.findByUserIdAndGroupId(command.newLeaderId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        validateApprovalStatus(newLeader);

        changeLeader(leader, newLeader);
    }

    public void validateCurrentLeader(Long userId, Long pastLeaderId) {
        if (!userId.equals(pastLeaderId)) {
            throw new ForbiddenException("해당 그룹 내의 리더가 아닙니다.");
        }
    }

    public void validateLeader(GroupMember leader) {
        if (!leader.isLeader()) {
            throw new ForbiddenException("리더만 권한을 위임할 수 있습니다.");
        }
    }

    public void validateApprovalStatus(GroupMember groupMember) {
        if (!groupMember.isApproved()) {
            throw new IllegalStateException("승인되지 않은 멤버입니다.");
        }
    }

    public void changeLeader(GroupMember leader, GroupMember newLeader) {
        leader.changeRole();
        newLeader.changeRole();
    }

    @Transactional
    public void expelMember(Long userId, GroupMemberCommand.Expel command) {
        GroupMember leader = groupMemberRepository.findByUserIdAndGroupId(userId,
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        validateLeader(leader);

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(command.userId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));

        groupMemberRepository.deleteByUserId(member.getId());
    }
}
