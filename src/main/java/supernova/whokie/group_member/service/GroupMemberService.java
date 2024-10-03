package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        GroupMember groupMember = command.toEntity(user, group);

        groupMemberRepository.save(groupMember);
    }
}
