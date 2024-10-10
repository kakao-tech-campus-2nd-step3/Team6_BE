package supernova.whokie.question;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import supernova.whokie.answer.controller.dto.AnswerRequest.Group;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.GroupRole;
import supernova.whokie.group_member.GroupStatus;
import supernova.whokie.group_member.infrastructure.repository.GroupMemberRepository;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.profiles.active=default",
        "jwt.secret=abcd",
        "spring.sql.init.mode=never"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

class QuestionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    void setUp() {

        Users user = Users.builder()
                .name("Test User")
                .email("test@example.com")
                .point(0)
                .age(20)
                .kakaoId(1234567890L)
                .gender(Gender.M)
                .imageUrl("default_image_url.jpg")
                .role(Role.USER)
                .build();
        userRepository.save(user);


        for (int i = 1; i <= 5; i++) {
            Users friendUser = Users.builder()
                    .name("Friend " + i)
                    .email("friend" + i + "@example.com")
                    .point(0)
                    .age(20)
                    .kakaoId(1234567890L + i)
                    .gender(Gender.F)
                    .imageUrl("default_image_url_friend_" + i + ".jpg")
                    .role(Role.USER)
                    .build();
            userRepository.save(friendUser);


            Friend friend = Friend.builder()
                    .hostUser(user)
                    .friendUser(friendUser)
                    .build();
            friendRepository.save(friend);
        }

        Groups group = Groups.builder()
                .groupName("test group")
                .groupImageUrl("test imageUrl")
                .description("test group desc")
                .build();
        groupRepository.save(group);

        //승인된 그룹질문
        for (int i = 1; i <= 10; i++) {
            Question question = Question.builder()
                    .id((long) i)
                    .content("Question " + i)
                    .writer(user)
                    .groupId(1L)
                    .questionStatus(QuestionStatus.APPROVED)
                    .build();
            questionRepository.save(question);
        }
        //거절된 그룹질문
        for (int i =11; i <= 20; i++) {
            Question question = Question.builder()
                    .id((long) i)
                    .content("Question " + i)
                    .writer(user)
                    .groupId(1L)
                    .questionStatus(QuestionStatus.REJECTED)
                    .build();
            questionRepository.save(question);
        }
        for (int i = 7; i <= 16; i++) {
            groupMemberRepository.save(GroupMember.builder()
                    .user(userRepository.save(
                            Users.builder()
                                    .name("Test User")
                                    .email("test" + i + "@example.com")
                                    .point(0)
                                    .age(20)
                                    .kakaoId(1234567890L)
                                    .gender(Gender.M)
                                    .imageUrl("default_image_url.jpg")
                                    .role(Role.USER)
                                    .build()
                    ))
                    .group(group)
                    .groupRole(GroupRole.MEMBER)
                    .groupStatus(GroupStatus.APPROVED)
                    .build());
        }

        groupMemberRepository.save(GroupMember.builder()
                .user(userRepository.save(
                        Users.builder()
                                .name("Test User")
                                .email("test" + 17 + "@example.com")
                                .point(0)
                                .age(20)
                                .kakaoId(1234567890L)
                                .gender(Gender.M)
                                .imageUrl("default_image_url.jpg")
                                .role(Role.USER)
                                .build()
                ))
                .group(group)
                .groupRole(GroupRole.LEADER)
                .groupStatus(GroupStatus.APPROVED)
                .build());

    }


    @Test
    @DisplayName("질문과 친구 목록을 정상적으로 가져오는지 테스트")
    void getCommonQuestionTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "1");

        mockMvc.perform(get("/api/common/question/random")
                        .requestAttr("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions.length()").value(10))
                .andExpect(jsonPath("$.questions[0].users.length()").value(5))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("questions 내용: " + responseContent);
                });
    }

    @Test
    @DisplayName("랜덤 그룹 질문 조회 테스트")
    void getGroupQuestionTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "7");

        mockMvc.perform(get("/api/group/{group-id}/question/random", 1L)
                .requestAttr("userId", "7")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.questions").isArray())
            .andExpect(jsonPath("$.questions.length()").value(10))
            .andExpect(jsonPath("$.questions[0].users.length()").value(5))
            .andDo(result -> {
                String responseContent = result.getResponse().getContentAsString();
                System.out.println("questions 내용: " + responseContent);
            });
    }

    @Test
    @DisplayName("그룹 질문 생성 테스트")
    void createGroupQuestion() throws Exception {
        String requestJson = """
            {
                "groupId": 1,
                "content": "Test question"
            }
        """;

        mockMvc.perform(post("/api/group/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .requestAttr("userId", "7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("질문이 성공적으로 생성되었습니다."))
            .andDo(print());
    }

    @Test
    @DisplayName("그룹 질문 승인 테스트")
    void approveGroupQuestion() throws Exception {
        String requestJson = """
            {
                "groupId": 1,
                "questionId": 1,
                "status" : true
            }
        """;

        mockMvc.perform(patch("/api/group/question/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .requestAttr("userId", String.valueOf(17)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("그룹 질문 승인에 성공하였습니다."))
            .andDo(print());
    }

    @Test
    @DisplayName("상태에 따른 질문 목록을 정상적으로 가져오는지 테스트 (APPROVED 상태)")
    void getGroupQuestionPagingApprovedTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "1");

        mockMvc.perform(get("/api/group/1/question")
                        .param("status", "true") // APPROVED 상태
                        .requestAttr("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("APPROVED 상태의 질문 목록: " + responseContent);
                });
    }

    @Test
    @DisplayName("상태에 따른 질문 목록을 정상적으로 가져오는지 테스트 (REJECTED 상태)")
    void getGroupQuestionPagingRejectedTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "1");

        mockMvc.perform(get("/api/group/1/question")
                        .param("status", "false") // REJECTED 상태
                        .requestAttr("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("REJECTED 상태의 질문 목록: " + responseContent);
                });
    }

}