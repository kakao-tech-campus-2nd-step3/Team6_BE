package supernova.whokie.group_member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;

@Service
@RequiredArgsConstructor
public class GroupMemberWriterService {

    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void save(GroupMember groupMember) {
        groupMemberRepository.save(groupMember);
    }

}
