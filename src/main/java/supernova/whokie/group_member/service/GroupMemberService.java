package supernova.whokie.group_member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;
import supernova.whokie.group_member.service.dto.GroupMemberModel.Members;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void delegateLeader(Long userId, GroupMemberCommand.Modify command) {
        validateCurrentLeader(userId, command.pastLeaderId());

        GroupMember leader = groupMemberRepository.findByUserIdAndGroupId(command.pastLeaderId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        leader.validateLeader();

        GroupMember newLeader = groupMemberRepository.findByUserIdAndGroupId(command.newLeaderId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        newLeader.validateApprovalStatus();

        changeLeader(leader, newLeader);
    }

    public void validateCurrentLeader(Long userId, Long pastLeaderId) {
        if (!userId.equals(pastLeaderId)) {
            throw new ForbiddenException("해당 그룹 내의 리더가 아닙니다.");
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
        leader.validateLeader();

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(command.userId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));

        groupMemberRepository.deleteByUserIdAndGroupId(member.getId(), command.groupId());
    }

    @Transactional(readOnly = true)
    public Members getGroupMembers(Long userId, Long groupId) {
        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
            .orElseThrow(() -> new EntityNotFoundException("해당 그룹의 유저만 조회할 수 있습니다."));

        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(groupId);
        return Members.from(groupMembers);
    }

    @Transactional
    public void joinGroup(GroupMemberCommand.Join command, Long userId) {
        if (groupMemberRepository.existsByUserIdAndGroupId(userId, command.groupId())) {
            throw new ForbiddenException("이미 가입한 그룹입니다.");
        }
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        Groups group = groupRepository.findById(command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("해당 그룹이 존재하지 않습니다."));

        GroupMember groupMember = command.toEntity(user, group);
        groupMemberRepository.save(groupMember);
    }

    /**
     * 1. 일반 멤버는 탈퇴 가능 2. 리더는 그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능
     */
    @Transactional
    public void exitGroup(GroupMemberCommand.Exit command, Long userId) {

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("해당 그룹의 유저만 탈퇴할 수 있습니다."));

        if (member.isLeader()) {
            Long groupMemberSize = groupMemberRepository.countByGroupId(command.groupId());
            if (groupMemberSize > 1) {
                throw new ForbiddenException("그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능합니다.");
            }
        }

        groupMemberRepository.deleteByUserIdAndGroupId(userId, command.groupId());
    }
}
