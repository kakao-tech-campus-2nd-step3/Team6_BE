package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;

@Service
@RequiredArgsConstructor
public class GroupMemberWriterService {

    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void save(GroupMember groupMember) {
        groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void expelMember(Long userId, GroupMemberCommand.Expel command) {
        GroupMember leader = groupMemberRepository.findByUserIdAndGroupId(userId,
                command.groupId())
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));
        leader.validateLeader();

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(command.userId(),
                command.groupId())
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        groupMemberRepository.deleteByUserIdAndGroupId(member.getId(), command.groupId());
    }

    /**
     * 1. 일반 멤버는 탈퇴 가능 2. 리더는 그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능
     */
    @Transactional
    public void exitGroup(GroupMemberCommand.Exit command, Long userId) {

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, command.groupId())
            .orElseThrow(
                () -> new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE));

        if (member.isLeader()) {
            Long groupMemberSize = groupMemberRepository.countByGroupId(command.groupId());
            if (groupMemberSize > 1) {
                throw new ForbiddenException("그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능합니다.");
            }
        }

        groupMemberRepository.deleteByUserIdAndGroupId(userId, command.groupId());
    }

}
