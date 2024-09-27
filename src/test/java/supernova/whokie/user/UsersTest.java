package supernova.whokie.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsersTest {

    @Test
    @DisplayName("Set에 UserId가 포함되는지 테스트")
    void isFriendTest() {
        // given
        Set<Long> set = Set.of(1L, 2L);
        Users user1 = Users.builder().id(1L).build();
        Users user3 = Users.builder().id(3L).build();

        // when
        boolean actual1 = set.contains(user1.getId());
        boolean actual2 = set.contains(user3.getId());

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }
}