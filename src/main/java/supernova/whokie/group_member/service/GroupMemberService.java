package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.AlreadyExistException;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupsRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UsersRepository;

@RequiredArgsConstructor
@Service
public class GroupMemberService {

    private final GroupsRepository groupsRepository;
    private final UsersRepository usersRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void addMemberToGroup(GroupMemberCommand.Create command) {
        Groups group = groupsRepository.findById(command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        Users user = usersRepository.findById(command.userId())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (groupMemberRepository.existsByUsersIdAndGroupsId(command.userId(), command.groupId())) {
            throw new AlreadyExistException("이미 가입된 그룹입니다.");
        }

        GroupMember groupMember = command.toEntity(user, group);

        groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void removeMemberFromGroup(Long groupId, Long userId) {
        groupsRepository.findById(groupId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        GroupMember groupMember = groupMemberRepository.findByUsersIdAndGroupsId(userId, groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹에 가입되어 있지 않습니다."));
        //TODO 그룹장 정책 필요
        groupMemberRepository.delete(groupMember);
    }
}
