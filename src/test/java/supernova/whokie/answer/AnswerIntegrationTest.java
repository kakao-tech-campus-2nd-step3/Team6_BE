package supernova.whokie.answer;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import supernova.whokie.answer.repository.AnswerRepository;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.group.Groups;
import supernova.whokie.group.repository.GroupRepository;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.repository.QuestionRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class AnswerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    void setUp() {
        Users user = createUser(0);

        createGroup(0);


        for (int i = 1; i <= 5; i++) {
            createFreindUser(i);
        }

        for (int i = 1; i <= 5; i++) {
            setFriendRelation(i, user);
        }

        for (int i = 1; i <= 5; i++) {
            Question question = createQuestion(i, user);

            // 답변 설정
            createAnswer(question, user);
        }

    }

    private void createAnswer(Question question, Users user) {
        Answer answer = Answer.builder()
                .question(question)
                .picker(userRepository.findById(2L).orElseThrow())
                .picked(user)
                .hintCount(2)
                .build();
        ReflectionTestUtils.setField(answer, "createdAt", LocalDateTime.of(2024, 9, 19, 0, 0));
        answerRepository.save(answer);
    }

    private Question createQuestion(int index, Users user) {
        Question question = Question.builder()
                .content("Test Question " + index)
                .questionStatus(QuestionStatus.APPROVED)
                .writer(user)
                .groupId(1L)
                .build();
        questionRepository.save(question);
        return question;
    }

    private void setFriendRelation(int index, Users user) {
        Users friendUser = userRepository.findById((long) index).orElseThrow();
        Friend friend = Friend.builder()
                .hostUser(user)
                .friendUser(friendUser)
                .build();
        friendRepository.save(friend);
    }

    private void createFreindUser(int index) {
        Users friendUser = Users.builder()
                .name("Friend " + index)
                .email("friend" + index + "@example.com")
                .point(100)
                .age(20)
                .kakaoId(1234567890L + index)
                .gender(Gender.F)
                .imageUrl("default_image_url_friend_" + index + ".jpg")
                .role(Role.USER)
                .build();

        userRepository.save(friendUser);
    }

    private void createGroup(int index) {
        Groups group = Groups.builder()
                .groupName("Test Group " + index)
                .description("Test Group " + index)
                .groupImageUrl("default_image_url.jpg")
                .build();
        groupRepository.save(group);
    }

    private Users createUser(int index) {
        Users user = Users.builder()
                .name("Test User " + index)
                .email("test@example.com")
                .point(100)
                .age(20)
                .kakaoId(1234567890L)
                .gender(Gender.M)
                .imageUrl("default_image_url.jpg")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return user;
    }


    @Test
    @DisplayName("답변 목록 새로고침 테스트")
    void refreshAnswerListTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "1");

        mockMvc.perform(get("/api/answer/refresh")
                        .requestAttr("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("users 내용: " + responseContent);
                });

    }

    @Test
    @DisplayName("공통 질문에 답변하기 테스트")
    void answerToCommonQuestionTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "1");

        Long userId = 1L;
        int initialPoint = userRepository.findById(userId).orElseThrow().getPoint();

        Long questionId = 1L;
        Long pickedId = 2L;

        String requestBody = String.format("{\"questionId\": %d, \"pickedId\": %d}", questionId,
                pickedId);

        mockMvc.perform(post("/api/answer/common")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .requestAttr("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("답변 완료"));
        Users userAfterAnswer = userRepository.findById(userId).orElseThrow();
        int finalPoint = userAfterAnswer.getPoint();
        assertThat(finalPoint).isEqualTo(initialPoint + Constants.ANSWER_POINT);
    }

    //@Test
    @DisplayName("전체 질문 기록 조회 테스트")
    void getAnswerRecordTest() throws Exception {
        // 별도의 더미 데이터 생성
        for (int i = 1; i <= 5; i++) {
            Question question = Question.builder()
                    .content("Custom Test Question " + i)
                    .build();
            questionRepository.save(question);

            Answer answer = Answer.builder()
                    .question(question)
                    .picker(userRepository.findById(1L).orElseThrow())
                    .picked(userRepository.findById(2L).orElseThrow())
                    .hintCount(3)
                    .build();
            ReflectionTestUtils.setField(answer, "createdAt",
                    LocalDateTime.of(2024, 9, 20, 0, 0)); // 날짜 설정
            answerRepository.save(answer);
        }
        mockMvc.perform(get("/api/answer/record")
                        .requestAttr("userId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("전체 질문 기록 내용: " + responseContent);
                });
    }

    @Test
    @DisplayName("Hints 조회 테스트")
    void getHintsTest() throws Exception {
        String answerId = "1";

        mockMvc.perform(get("/api/answer/hint/{answer-id}", answerId)
                        .requestAttr("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hints").isArray())
                .andExpect(jsonPath("$.hints.length()").value(3))
                .andExpect(jsonPath("$.hints[0].valid").value(true))
                .andExpect(jsonPath("$.hints[1].valid").value(true))
                .andExpect(jsonPath("$.hints[2].valid").value(false))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("Hints 내용: " + responseContent);
                });
    }

    @Test
    @DisplayName("힌트 구매 테스트")
    void purchaseHintTest() throws Exception {
        Long answerId = 1L;
        Long userId = 1L;
        int hintPurchasePoint = 20;

        int initialPoint = userRepository.findById(userId).orElseThrow().getPoint();

        String requestBody = String.format("{\"answerId\": %d}", answerId);

        mockMvc.perform(post("/api/answer/hint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .requestAttr("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("힌트를 성공적으로 구매하였습니다!"))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("구매 응답 내용: " + responseContent);
                });

        //유저 포인트 감소 확인
        Users userAfterPurchase = userRepository.findById(userId).orElseThrow();
        int finalPoint = userAfterPurchase.getPoint();
        assertThat(finalPoint).isEqualTo(initialPoint - hintPurchasePoint);
    }

    @Test
    @DisplayName("그룹 질문에 답변하기 테스트")
    void answerToGroupQuestionTest() throws Exception {
        Long userId = 1L;
        Long questionId = 1L;
        Long groupId = 1L;
        Long pickedId = 2L;

        String requestBody = String.format(
                "{\"questionId\": %d, \"groupId\": %d, \"pickedId\": %d}",
                questionId, groupId, pickedId
        );
        mockMvc.perform(post("/api/answer/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .requestAttr("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 질문 답변 완료"))
                .andDo(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    System.out.println("그룹 질문 답변 내용: " + responseContent);
                });

        Users userAfterAnswer = userRepository.findById(userId).orElseThrow();
        int finalPoint = userAfterAnswer.getPoint();
        assertThat(finalPoint).isEqualTo(100 + Constants.ANSWER_POINT);
    }

}
