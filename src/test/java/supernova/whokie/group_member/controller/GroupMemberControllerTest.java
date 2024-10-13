package supernova.whokie.group_member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtInterceptor;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UsersRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "spring.profiles.active=default",
    "jwt.secret=abcd",
    "spring.sql.init.mode=never"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GroupMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GroupRepository groupRepository;

    private Users user1;
    private Users user2;
    private Groups group;
    private GroupMember leader;
    private GroupMember member;
    private Long pastLeaderId;
    private Long newLeaderId;
    private Long groupId;

    @BeforeEach
    void setUp() {
        user1 = createUser(1, 1500, 22);

        user2 = createUser(2, 1000, 25);

        pastLeaderId = user1.getId();
        newLeaderId = user2.getId();

        group = createGroup(1);

        groupId = group.getId();

        leader = createGroupMember(user1, group, GroupRole.LEADER);

        member = createGroupMember(user2, group, GroupRole.MEMBER);
    }



    @Test
    @DisplayName("그룹장 위임 테스트")
    void delegateLeader() throws Exception {
        String requestJson = String.format(
            "{\"groupId\": %d, \"newLeaderId\": %d, \"pastLeaderId\": %d}", groupId, newLeaderId,
            pastLeaderId);
        String token = jwtProvider.createToken(1L, Role.USER);
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(patch("/api/group/leader")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .requestAttr("userId", String.valueOf(1L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("그룹장 위임에 성공하였습니다."))
            .andDo(print());

        GroupMember updatedLeader = groupMemberRepository.findByUserIdAndGroupId(pastLeaderId,
                groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        GroupMember updatedNewLeader = groupMemberRepository.findByUserIdAndGroupId(newLeaderId,
                groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));

        assertThat(updatedLeader.getGroupRole()).isEqualTo(GroupRole.MEMBER);
        assertThat(updatedNewLeader.getGroupRole()).isEqualTo(GroupRole.LEADER);
    }

    @Test
    @DisplayName("그룹 내 멤버 강퇴 테스트")
    void expelMember() throws Exception {
        String requestJson = String.format("{\"groupId\": %d, \"userId\": %d}", groupId,
            member.getId());
        String token = jwtProvider.createToken(1L, Role.USER);
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(post("/api/group/expel")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .requestAttr("userId", String.valueOf(1L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("그룹 멤버 강퇴에 성공하였습니다."))
            .andDo(print());

        List<GroupMember> groupMembers = groupMemberRepository.findAll();

        assertThat(groupMembers.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("그룹 멤버 조회 테스트")
    void getGroupMembers_success() throws Exception {
        String token = jwtProvider.createToken(1L, Role.USER);
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(get("/api/group/{group-id}/member", group.getId())
                .header("Authorization", "Bearer " + token)
                .requestAttr("userId", String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.members").isArray())
            .andExpect(jsonPath("$.members[0].groupMemberId").value(leader.getId()))
            .andExpect(jsonPath("$.members[0].userId").value(user1.getId()))
            .andExpect(jsonPath("$.members[0].role").value(leader.getGroupRole().toString()))
            .andExpect(jsonPath("$.members[0].userName").value(user1.getName()))
            .andExpect(jsonPath("$.members[0].joinedAt").value(
                leader.getCreatedAt().toLocalDate().toString()))
            .andExpect(jsonPath("$.members[1].groupMemberId").value(member.getId()))
            .andExpect(jsonPath("$.members[1].userId").value(user2.getId()))
            .andExpect(jsonPath("$.members[1].role").value(member.getGroupRole().toString()))
            .andExpect(jsonPath("$.members[1].userName").value(user2.getName()))
            .andExpect(jsonPath("$.members[1].joinedAt").value(
                member.getCreatedAt().toLocalDate().toString()))
            .andExpect(status().isOk())
            .andDo(print());
    }

    private GroupMember createGroupMember(Users user, Groups group, GroupRole role) {
        return groupMemberRepository.save(GroupMember.builder()
                .user(user)
                .group(group)
                .groupRole(role)
                .groupStatus(GroupStatus.APPROVED)
                .build());
    }

    private Groups createGroup(int index) {
        return groupRepository.save(Groups.builder()
                .groupName("test " + index)
                .description("test" + index)
                .groupImageUrl("tset" + index)
                .build());
    }

    private Users createUser(int index, int point, int age) {
        return usersRepository.save(Users.builder()
                .name("test" + index)
                .email("test" + index + "@gmail.com")
                .point(point)
                .age(age)
                .kakaoId(1L)
                .gender(Gender.M)
                .role(Role.USER)
                .build());
    }
}
