package supernova.whokie.group_member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
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
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
        leader.validateLeader();

        GroupMember newLeader = groupMemberRepository.findByUserIdAndGroupId(command.newLeaderId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
        newLeader.validateApprovalStatus();

        changeLeader(leader, newLeader);
    }

    public void validateCurrentLeader(Long userId, Long pastLeaderId) {
        if (!userId.equals(pastLeaderId)) {
            throw new ForbiddenException(MessageConstants.NOT_GROUP_LEADER_MESSAGE);
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
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
        leader.validateLeader();

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(command.userId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        groupMemberRepository.deleteByUserIdAndGroupId(member.getId(), command.groupId());
    }

    @Transactional(readOnly = true)
    public Members getGroupMembers(Long userId, Long groupId) {
        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(groupId);
        return Members.from(groupMembers);
    }

    @Transactional
    public void joinGroup(GroupMemberCommand.Join command, Long userId) {
        if (groupMemberRepository.existsByUserIdAndGroupId(userId, command.groupId())) {
            throw new ForbiddenException(MessageConstants.ALREADY_GROUP_MEMBER_MESSAGE);
        }
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        Groups group = groupRepository.findById(command.groupId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_NOT_FOUND_MESSAGE));

        GroupMember groupMember = command.toEntity(user, group);
        groupMemberRepository.save(groupMember);
    }

    /**
     * 1. 일반 멤버는 탈퇴 가능 2. 리더는 그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능
     */
    @Transactional
    public void exitGroup(GroupMemberCommand.Exit command, Long userId) {

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        if (member.isLeader()) {
            Long groupMemberSize = groupMemberRepository.countByGroupId(command.groupId());
            if (groupMemberSize > 1) {
                throw new ForbiddenException("그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능합니다.");
            }
        }

        groupMemberRepository.deleteByUserIdAndGroupId(userId, command.groupId());
    }
}
