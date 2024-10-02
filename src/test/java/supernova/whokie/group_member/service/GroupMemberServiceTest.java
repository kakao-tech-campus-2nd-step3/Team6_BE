package supernova.whokie.group_member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.group.Groups;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceTest {

    @InjectMocks
    private GroupMemberService groupMemberService;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    private GroupMember leader;
    private GroupMember member;
    private Users user1;
    private Users user2;
    private Groups group;
    private Long userId = 1L;
    private Long pastLeaderId = 1L;
    private Long newLeaderId = 2L;
    private Long groupId = 1L;

    @BeforeEach
    void setUp() {
        user1 = Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(1000)
            .age(30)
            .kakaoId(1L)
            .gender(Gender.M)
            .imageUrl("test")
            .role(Role.USER)
            .build();

        user2 = Users.builder()
            .id(2L)
            .name("test")
            .email("test@gmail.com")
            .point(1000)
            .age(30)
            .kakaoId(1L)
            .gender(Gender.M)
            .imageUrl("test")
            .role(Role.USER)
            .build();

        group = Groups.builder()
            .id(groupId)
            .groupName("test")
            .description("test")
            .groupImageUrl("test")
            .build();

        leader = GroupMember.builder()
            .id(pastLeaderId)
            .user(user1)
            .group(group)
            .groupRole(GroupRole.LEADER)
            .groupStatus(GroupStatus.APPROVED)
            .build();

        member = GroupMember.builder()
            .id(newLeaderId)
            .user(user2)
            .group(group)
            .groupRole(GroupRole.MEMBER)
            .groupStatus(GroupStatus.APPROVED)
            .build();
    }

    @Test
    @DisplayName("그룹장 위임")
    void delegateLeader() {
        // given
        GroupMemberCommand.Modify command = new GroupMemberCommand.Modify(groupId, pastLeaderId, newLeaderId);
        given(groupMemberRepository.findByUserIdAndGroupId(pastLeaderId, groupId))
            .willReturn(Optional.of(leader));

        given(groupMemberRepository.findByUserIdAndGroupId(newLeaderId, groupId))
            .willReturn(Optional.of(member));

        // when
        groupMemberService.delegateLeader(userId, command);

        // then
        assertThat(leader.getGroupRole()).isEqualTo(GroupRole.MEMBER);
        assertThat(member.getGroupRole()).isEqualTo(GroupRole.LEADER);
    }

    @Test
    @DisplayName("그룹 내 멤버 강퇴")
    void expelMember() {
        // given
        GroupMemberCommand.Expel command = new GroupMemberCommand.Expel(groupId, member.getId());
        given(groupMemberRepository.findByUserIdAndGroupId(leader.getId(), groupId))
            .willReturn(Optional.of(leader));

        given(groupMemberRepository.findByUserIdAndGroupId(member.getId(), groupId))
            .willReturn(Optional.of(member));

        doNothing().when(groupMemberRepository).deleteByUserIdAndGroupId(member.getId(), command.groupId());

        // when
        groupMemberService.expelMember(1L, command);

        // then
        verify(groupMemberRepository).deleteByUserIdAndGroupId(member.getId(), command.groupId());
    }

}
