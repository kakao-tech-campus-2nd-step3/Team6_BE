package supernova.whokie.group_member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtInterceptor;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupsRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.group_member.service.GroupMemberService;
import supernova.whokie.group_member.service.dto.GroupMemberCommand;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UsersRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    private GroupsRepository groupsRepository;

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
        user1 = usersRepository.save(Users.builder()
            .name("test1")
            .email("test1@gmail.com")
            .point(1500)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build());

        user2 = usersRepository.save(Users.builder()
            .name("test2")
            .email("test2@gmail.com")
            .point(1000)
            .age(25)
            .kakaoId(1L)
            .gender(Gender.F)
            .role(Role.USER)
            .build());

        pastLeaderId = user1.getId();
        newLeaderId = user2.getId();

        group = groupsRepository.save(Groups.builder()
            .groupName("test")
            .description("test")
            .groupImageUrl("tset")
            .build());

        groupId = group.getId();

        leader = groupMemberRepository.save(GroupMember.builder()
            .user(user1)
            .group(group)
            .groupRole(GroupRole.LEADER)
            .groupStatus(GroupStatus.APPROVED)
            .build());

        member = groupMemberRepository.save(GroupMember.builder()
            .user(user2)
            .group(group)
            .groupRole(GroupRole.MEMBER)
            .groupStatus(GroupStatus.APPROVED)
            .build());
    }

    @Test
    @DisplayName("그룹장 위임 테스트")
    void delegateLeader() throws Exception {
        String requestJson = String.format("{\"groupId\": %d, \"newLeaderId\": %d, \"pastLeaderId\": %d}", groupId, newLeaderId, pastLeaderId);
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

        GroupMember updatedLeader = groupMemberRepository.findByUserIdAndGroupId(pastLeaderId, groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        GroupMember updatedNewLeader = groupMemberRepository.findByUserIdAndGroupId(newLeaderId, groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));

        assertThat(updatedLeader.getGroupRole()).isEqualTo(GroupRole.MEMBER);
        assertThat(updatedNewLeader.getGroupRole()).isEqualTo(GroupRole.LEADER);
    }

    @Test
    @DisplayName("그룹 내 멤버 강퇴 테스트")
    void expelMember() throws Exception {
        String requestJson = String.format("{\"groupId\": %d, \"userId\": %d}", groupId, member.getId());
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
}
