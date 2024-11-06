package supernova.whokie.group.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.constants.GroupConstants;
import supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupCommand;
import supernova.whokie.group.service.dto.GroupModel;
import supernova.whokie.group.service.dto.GroupModel.InfoWithMemberCount;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.service.GroupMemberReaderService;
import supernova.whokie.groupmember.service.GroupMemberWriterService;
import supernova.whokie.groupmember.util.InviteCodeUtil;
import supernova.whokie.s3.event.S3EventDto;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.s3.util.S3Util;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupWriterService groupWriterService;
    private final GroupReaderService groupReaderService;
    private final UserReaderService userReaderService;
    private final GroupMemberWriterService groupMemberWriterService;
    private final GroupMemberReaderService groupMemberReaderService;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Service s3Service;

    /**
     * 그룹 생성후, 그룹장을 생성한 유저로 지정한다.
     */
    @Transactional
    public GroupModel.Info createGroup(GroupCommand.Create command, Long userId) {

        var group = command.toEntity();
        groupWriterService.save(group);

        var user = userReaderService.getUserById(userId);

        GroupMember leader = GroupMember.CreateLeader(user, group);
        groupMemberWriterService.save(leader);
        return GroupModel.Info.from(group);
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

    @Transactional
    public void modifyGroupImage(Long userId, Long groupId, MultipartFile image) {
        GroupMember leader = groupMemberReaderService.getByUserIdAndGroupId(userId, groupId);
        leader.validateLeader();
        String key = S3Util.generateS3Key(GroupConstants.GROUP_IMAGE_FOLDER, groupId);
        S3EventDto.Upload event = S3EventDto.Upload.toDto(image, key,
            GroupConstants.GROUP_IMAGE_WIDTH, GroupConstants.GROUP_IMAGE_HEIGHT);
        eventPublisher.publishEvent(event);

        Groups group = groupReaderService.getGroupById(groupId);
        group.modifyImageUrl(key);
    }

    @Transactional(readOnly = true)
    public InfoWithMemberCount getGroupInfo(Long groupId) {

        GroupInfoWithMemberCount groupInfo = groupReaderService.getGroupInfoWithMemberCountByGroupId(
            groupId);
        String imageUrl = s3Service.getSignedUrl(groupInfo.getGroupImageUrl());
        return InfoWithMemberCount.from(groupInfo, imageUrl);
    }

    @Transactional(readOnly = true)
    public Page<InfoWithMemberCount> getGroupPaging(Long userId, Pageable pageable) {
        Page<GroupInfoWithMemberCount> groupPage = groupReaderService.getGroupPaging(userId,
            pageable);
        return groupPage.map(group -> {
            String imageUrl = s3Service.getSignedUrl(group.getGroupImageUrl());
            return InfoWithMemberCount.from(group, imageUrl);
        });
    }

    @Transactional
    public GroupModel.InviteCode inviteGroup(Long userId, Long groupId) {
        if (!groupReaderService.isGroupExist(groupId)) {
            throw new ForbiddenException(MessageConstants.GROUP_NOT_FOUND_MESSAGE);
        }
        if (!groupMemberReaderService.isGroupMemberExist(userId, groupId)) {
            throw new ForbiddenException(MessageConstants.GROUP_MEMBER_NOT_FOUND_MESSAGE);
        }
        String inviteCode = InviteCodeUtil.createCode(groupId, LocalDateTime.now(),
            LocalDateTime.now().plusDays(7));
        return GroupModel.InviteCode.from(inviteCode);
    }
}
