package supernova.whokie.answer.constants;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AnswerConstants {
    public static final int ANSWER_POINT = 5;
    public static final int DEFAULT_HINT_COUNT = 0;
    public static final int MAX_HINT_COUNT = 3;
    public static final LocalDateTime DEFAULT_START_DATE = LocalDate.of(1990, 1, 1).atStartOfDay();

    public static final char[] CHO_SUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };
    private AnswerConstants() {

    }
}
