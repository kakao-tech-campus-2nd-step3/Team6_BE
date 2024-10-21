package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupCommand;
import supernova.whokie.group.service.dto.GroupModel.InfoWithMemberCount;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.service.GroupMemberReaderService;
import supernova.whokie.group_member.service.GroupMemberWriterService;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupWriterService groupWriterService;
    private final GroupReaderService groupReaderService;
    private final UserReaderService userReaderService;
    private final GroupMemberWriterService groupMemberWriterService;
    private final GroupMemberReaderService groupMemberReaderService;

    /**
     * 그룹 생성후, 그룹장을 생성한 유저로 지정한다.
     */
    @Transactional
    public void createGroup(GroupCommand.Create command, Long userId) {

        var group = command.toEntity();
        groupWriterService.save(group);

        var user = userReaderService.getUserById(userId);

        GroupMember leader = GroupMember.CreateLeader(user, group);
        groupMemberWriterService.save(leader);
    }


    @Transactional
    public void modifyGroup(Long userId, GroupCommand.Modify command) {
        GroupMember groupMember = groupMemberReaderService.getByUserIdAndGroupId(userId,
            command.groupId());

        if (!groupMember.isLeader()) {
            throw new ForbiddenException(MessageConstants.NOT_GROUP_LEADER_MESSAGE);
        }
        Groups group = groupReaderService.getGroupById(command.groupId());

        group.modify(command.groupName(), command.description());
    }

    @Transactional(readOnly = true)
    public InfoWithMemberCount getGroupInfo(Long groupId) {

        GroupInfoWithMemberCount groupInfo = groupReaderService.getGroupInfoWithMemberCountByGroupId(
            groupId);
        return InfoWithMemberCount.from(groupInfo);
    }

    @Transactional(readOnly = true)
    public Page<InfoWithMemberCount> getGroupPaging(Long userId, Pageable pageable) {
        Page<GroupInfoWithMemberCount> groupPage = groupReaderService.getGroupPaging(userId,
            pageable);
        return groupPage.map(InfoWithMemberCount::from);
    }
}
