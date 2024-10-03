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
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


        for (int i = 1; i <= 10; i++) {
            Question question = Question.builder()
                    .id((long) i)
                    .content("Question " + i)
                    .writer(user)
                    .questionStatus(QuestionStatus.APPROVED)
                    .build();
            questionRepository.save(question);
        }

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
}