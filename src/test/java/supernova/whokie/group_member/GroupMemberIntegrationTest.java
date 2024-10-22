package supernova.whokie.group_member;

import io.awspring.cloud.s3.S3Template;
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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "jwt.secret=abcd",
    "spring.sql.init.mode=never"
})
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GroupMemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository usersRepository;

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
        user1 = createUser("test1@gmail.com");
        user2 = createUser("test2@gmail.com");

        pastLeaderId = user1.getId();
        newLeaderId = user2.getId();

        group = createGroup();
        groupId = group.getId();

        leader = createGroupMember(user1, GroupRole.LEADER);
        member = createGroupMember(user2, GroupRole.MEMBER);
    }

    @Test
    @DisplayName("그룹장 위임 테스트")
    void delegateLeader() throws Exception {
        String requestJson = String.format(
            "{\"groupId\": %d, \"newLeaderId\": %d, \"pastLeaderId\": %d}", groupId, newLeaderId,
            pastLeaderId);

        mockMvc.perform(patch("/api/group/leader")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .requestAttr("userId", String.valueOf(user1.getId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("그룹장 위임에 성공하였습니다."))
            .andDo(print());

        GroupMember updatedLeader = groupMemberRepository.findByUserIdAndGroupId(pastLeaderId,
                groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
        GroupMember updatedNewLeader = groupMemberRepository.findByUserIdAndGroupId(newLeaderId,
                groupId)
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));

        assertAll(
            () -> assertThat(updatedLeader.getGroupRole()).isEqualTo(GroupRole.MEMBER),
            () -> assertThat(updatedNewLeader.getGroupRole()).isEqualTo(GroupRole.LEADER)
        );
    }

    @Test
    @DisplayName("그룹 내 멤버 강퇴 테스트")
    void expelMember() throws Exception {
        String requestJson = String.format("{\"groupId\": %d, \"userId\": %d}", groupId,
            member.getId());

        mockMvc.perform(post("/api/group/expel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .requestAttr("userId", String.valueOf(user1.getId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("그룹 멤버 강퇴에 성공하였습니다."))
            .andDo(print());

        List<GroupMember> groupMembers = groupMemberRepository.findAll();

        assertThat(groupMembers.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("그룹 멤버 조회 테스트")
    void getGroupMembers_success() throws Exception {
        mockMvc.perform(get("/api/group/{group-id}/member", group.getId())
                .requestAttr("userId", String.valueOf(user1.getId()))
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

    private Users createUser(String email) {
        Users user = Users.builder()
            .name("test")
            .email(email)
            .point(1500)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();

        usersRepository.save(user);
        return user;
    }

    private Groups createGroup() {
        Groups group = Groups.builder()
            .groupName("test")
            .description("test")
            .groupImageUrl("tset")
            .build();

        groupRepository.save(group);
        return group;
    }

    private GroupMember createGroupMember(Users user, GroupRole groupRole) {
        GroupMember groupMember = GroupMember.builder()
            .user(user)
            .group(group)
            .groupRole(groupRole)
            .groupStatus(GroupStatus.APPROVED)
            .build();

        groupMemberRepository.save(groupMember);
        return groupMember;
    }
}
