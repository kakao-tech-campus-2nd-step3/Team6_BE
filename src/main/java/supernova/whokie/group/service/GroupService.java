package supernova.whokie.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupsRepository;
import supernova.whokie.group.infrastructure.repository.dto.GroupInfoWithMemberCount;
import supernova.whokie.group.service.dto.GroupCommand;
import supernova.whokie.group.service.dto.GroupModel;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupsRepository groupsRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public Long createGroup(GroupCommand.Create command) {
        Groups group = command.toEntity();
        groupsRepository.save(group);
        return group.getId();
    }

    @Transactional(readOnly = true)
    public Page<GroupModel.InfoWithMemberCount> getGroups(Pageable pageable) {
        Page<GroupInfoWithMemberCount> page = groupsRepository.findGroupsWithMemberCount(pageable);

        return page.map(GroupModel.InfoWithMemberCount::from);
    }

    @Transactional
    public void modifyGroup(GroupCommand.Modify command, Long userId) {
        GroupMember groupMember = groupMemberRepository.findByUsersIdAndGroupsId(userId,
                command.groupId())
            .orElseThrow(() -> new IllegalArgumentException("그룹에 가입되어 있지 않습니다."));

        Groups group = groupsRepository.findById(command.groupId())
            .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));

        if (!groupMember.isLeader()) {
            throw new IllegalArgumentException("그룹의 리더만 그룹 정보를 수정할 수 있습니다.");
        }

        group.modify(command.groupName(), command.description());
    }
}
