package supernova.whokie.groupmember.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.group.Groups;
import supernova.whokie.groupmember.GroupMember;
import supernova.whokie.groupmember.GroupRole;
import supernova.whokie.groupmember.GroupStatus;
import supernova.whokie.groupmember.service.dto.GroupMemberCommand;
import supernova.whokie.groupmember.service.dto.GroupMemberModel;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceTest {

    @InjectMocks
    private GroupMemberService groupMemberService;

    @Mock
    private GroupMemberWriterService groupMemberWriterService;

    @Mock
    private GroupMemberReaderService groupMemberReaderService;

    private GroupMember leader;
    private GroupMember member;
    private Users user1;
    private Users user2;
    private Groups group;
    private Long userId;
    private Long pastLeaderId;
    private Long newLeaderId;
    private Long groupId;

    @BeforeEach
    void setUp() {
        user1 = createUser("test1@gmail.com", 1L);
        user2 = createUser("test2@gmail.com", 2L);

        userId = user1.getId();
        pastLeaderId = user1.getId();
        newLeaderId = user2.getId();

        group = createGroup();
        groupId = group.getId();

        leader = createGroupMember(user1, GroupRole.LEADER, 1L);
        member = createGroupMember(user2, GroupRole.MEMBER, 2L);
    }

    @Test
    @DisplayName("그룹장 위임")
    void delegateLeader() {
        // given
        GroupMemberCommand.Modify command = new GroupMemberCommand.Modify(groupId, pastLeaderId,
            newLeaderId);

        given(groupMemberReaderService.getByUserIdAndGroupId(pastLeaderId, groupId))
            .willReturn(leader);

        given(groupMemberReaderService.getByUserIdAndGroupId(newLeaderId, groupId))
            .willReturn(member);

        // when
        groupMemberService.delegateLeader(userId, command);

        // then
        assertAll(
            () -> assertThat(leader.getGroupRole()).isEqualTo(GroupRole.MEMBER),
            () -> assertThat(member.getGroupRole()).isEqualTo(GroupRole.LEADER)
        );
    }

    @Test
    @DisplayName("그룹 내 멤버 강퇴")
    void expelMember() {
        // given
        GroupMemberCommand.Expel command = new GroupMemberCommand.Expel(groupId, member.getId());
        given(groupMemberReaderService.getByUserIdAndGroupId(leader.getId(), groupId))
            .willReturn(leader);

        given(groupMemberReaderService.isGroupMemberExist(member.getId(), groupId))
            .willReturn(true);

        // when
        groupMemberService.expelMember(leader.getId(), command);

        // then
        verify(groupMemberWriterService).expelMember(member.getId(), command.groupId());
    }

    @Test
    @DisplayName("그룹 내 멤버 조회")
    void getGroupMembers() throws Exception {
        // given
        Field createdAtField = BaseTimeEntity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(leader, LocalDateTime.now());
        createdAtField.set(member, LocalDateTime.now());

        given(groupMemberReaderService.getGroupMembers(userId, groupId))
            .willReturn(List.of(leader, member));

        // when
        GroupMemberModel.Members members = groupMemberService.getGroupMembers(userId,
            groupId);

        // then
        assertAll(
            () -> assertThat(members.members()).hasSize(2),
            () -> assertThat(members.members().get(0).userId()).isEqualTo(user1.getId()),
            () -> assertThat(members.members().get(0).userName()).isEqualTo(user1.getName()),
            () -> assertThat(members.members().get(0).role()).isEqualTo(GroupRole.LEADER),
            () -> assertThat(members.members().get(1).userId()).isEqualTo(user2.getId()),
            () -> assertThat(members.members().get(1).userName()).isEqualTo(user2.getName()),
            () -> assertThat(members.members().get(1).role()).isEqualTo(GroupRole.MEMBER)
           /* () -> verify(groupMemberRepository).existsByUserIdAndGroupId(userId, groupId),     이거 왜 안됨?
            () -> verify(groupMemberRepository).findAllByGroupId(groupId)*/
        );
    }

    private Users createUser(String email, Long id) {
        return Users.builder()
            .id(id)
            .name("test")
            .email(email)
            .point(1500)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();
    }

    private Groups createGroup() {
        return Groups.builder()
            .id(1L)
            .groupName("test")
            .description("test")
            .groupImageUrl("tset")
            .build();
    }

    private GroupMember createGroupMember(Users user, GroupRole groupRole, Long id) {
        return GroupMember.builder()
            .id(id)
            .user(user)
            .group(group)
            .groupRole(groupRole)
            .groupStatus(GroupStatus.APPROVED)
            .build();
    }
}
