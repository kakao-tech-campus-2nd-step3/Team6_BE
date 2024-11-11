package supernova.whokie.groupmember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.global.exception.InvalidConditionException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.service.GroupReaderService;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.service.dto.GroupMemberCommand;
import supernova.whokie.groupmember.service.dto.GroupMemberModel;
import supernova.whokie.groupmember.util.CodeData;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberWriterService groupMemberWriterService;
    private final GroupMemberReaderService groupMemberReaderService;
    private final UserReaderService userReaderService;
    private final GroupReaderService groupReaderService;
    private final S3Service s3Service;

    @Transactional
    public void delegateLeader(Long userId, GroupMemberCommand.Modify command) {
        validateCurrentLeader(userId, command.pastLeaderId());

        GroupMember leader = groupMemberReaderService.getByUserIdAndGroupId(command.pastLeaderId(),
            command.groupId());

        leader.validateDelegateLeader();

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
        CodeData codeData = command.getUrlData();

        if (groupMemberReaderService.isGroupMemberExist(userId, codeData.groupId())) {
            throw new ForbiddenException(MessageConstants.ALREADY_GROUP_MEMBER_MESSAGE);
        }

        Users user = userReaderService.getUserById(userId);
        Groups group = groupReaderService.getGroupById(codeData.groupId());
        GroupMember groupMember = command.toEntity(user, group);
        groupMemberWriterService.save(groupMember);
    }


    @Transactional(readOnly = true)
    public Page<GroupMemberModel.Member> getGroupMemberPaging(Pageable pageable, Long userId,
                                                              Long groupId) {
        Page<GroupMember> groupMembers = groupMemberReaderService.getGroupMemberPaging(pageable, userId,
            groupId);
        return groupMembers.map(entity -> {
            String imageUrl = entity.getUser().getImageUrl();
            if (entity.getUser().isImageUrlStoredInS3()) {
                imageUrl = s3Service.getSignedUrl(imageUrl);
            }
            return GroupMemberModel.Member.from(entity, imageUrl);
        });
    }

    @Transactional(readOnly = true)
    public List<GroupMemberModel.Member> getGroupMemberList(Long userId, Long groupId) {
        List<GroupMember> groupMembers = groupMemberReaderService.getGroupMembersList(userId, groupId);

        return groupMembers.stream().map(entity -> {
            String imageUrl = entity.getUser().getImageUrl();
            if (entity.getUser().isImageUrlStoredInS3()) {
                imageUrl = s3Service.getSignedUrl(imageUrl);
            }
            return GroupMemberModel.Member.from(entity, imageUrl);
        }).toList();
    }

    @Transactional
    public void expelMember(Long userId, GroupMemberCommand.Expel command) {
        GroupMember leader = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());
        leader.validateLeaderExpelAutority();
        checkGroupMemberExist(command.userId(), command.groupId());
        groupMemberWriterService.expelMember(command.userId(), command.groupId());
    }

    @Transactional(readOnly = true)
    public void checkGroupMemberExist(Long userId, Long groupId) {
        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new EntityNotFoundException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * 1. 일반 멤버는 탈퇴 가능 2. 리더는 그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능
     */
    @Transactional
    public void exitGroup(GroupMemberCommand.Exit command, Long userId) {
        GroupMember member = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());

        if (member.isLeader()) {
            Long groupMemberSize = groupMemberReaderService.groupMemberCountByGroupId(
                command.groupId());
            if (groupMemberSize > 1) {
                throw new InvalidConditionException("그룹에 속한 멤버가 본인 한명일 경우에 탈퇴 가능합니다.");
            }
        }
        groupMemberWriterService.deleteByUserIdAndGroupId(command.groupId(), userId);
    }

    @Transactional(readOnly = true)
    public GroupMemberModel.Role getGroupMemberRole(Long userId, Long groupId) {
        GroupMember member = groupMemberReaderService.getByUserIdAndGroupId(userId, groupId);
        return GroupMemberModel.Role.from(member.getGroupRole());
    }

}
