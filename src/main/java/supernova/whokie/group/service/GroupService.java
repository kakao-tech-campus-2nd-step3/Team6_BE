package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupCommand;
import supernova.whokie.group.service.dto.GroupModel.InfoWithMemberCount;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    /**
     * 그룹 생성후, 그룹장을 생성한 유저로 지정한다.
     */
    @Transactional
    public void createGroup(GroupCommand.Create command, Long userId) {

        var group = command.toEntity();
        groupRepository.save(group);

        var user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        GroupMember leader = GroupMember.CreateLeader(user, group);
        groupMemberRepository.save(leader);
    }

    public Page<InfoWithMemberCount> getGroupPaging(Long userId, Pageable pageable) {
        Page<GroupInfoWithMemberCount> groupPage = groupRepository.findGroupsWithMemberCountByUserId(
            userId,
            pageable);
        return groupPage.map(InfoWithMemberCount::from);
    }
}
