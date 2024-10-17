package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.service.GroupReaderService;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberWriterService groupMemberWriterService;
    private final GroupMemberReaderService groupMemberReaderService;
    private final UserReaderService userReaderService;
    private final GroupReaderService groupReaderService;

    @Transactional
    public void delegateLeader(Long userId, GroupMemberCommand.Modify command) {
        validateCurrentLeader(userId, command.pastLeaderId());

        GroupMember leader = groupMemberReaderService.getByUserIdAndGroupId(command.pastLeaderId(),
            command.groupId());

        leader.validateLeader();

        GroupMember newLeader = groupMemberReaderService.getByUserIdAndGroupId(
            command.newLeaderId(), command.groupId());

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
    public void joinGroup(GroupMemberCommand.Join command, Long userId) {
        if (groupMemberReaderService.isGroupMemberExist(userId, command.groupId())) {
            throw new ForbiddenException(MessageConstants.ALREADY_GROUP_MEMBER_MESSAGE);
        }
        Users user = userReaderService.getUserById(userId);
        Groups group = groupReaderService.getGroupById(command.groupId());
        GroupMember groupMember = command.toEntity(user, group);
        groupMemberWriterService.save(groupMember);
    }


}
