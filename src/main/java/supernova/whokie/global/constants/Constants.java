package supernova.whokie.global.constants;

import java.util.Map;

public final class Constants {
    public static final int ANSWER_POINT = 5;
    public static final int DEFAULT_HINT_COUNT = 0;
    public static final Map<String, String> FILE_TYPE = Map.of("image/png", "png", "image/jpeg", "jpg");
    public static final int FIRST_HINT_PURCHASE_POINT = 10;
    public static final int FRIEND_LIMIT = 5;
    public static final String GROUP_IMAGE_FOLDER = "group";
    public static final int MAX_HINT_COUNT = 3;
    public static final String POINT_EARN_MESSAGE = "적립";
    public static final String PROFILE_BG_IMAGE_FOLRDER = "profile_bg";
    public static final String USER_IMAGE_FOLRDER = "user";
    public static final int QUESTION_LIMIT = 10;
    public static final int SECOND_HINT_PURCHASE_POINT = 20;
    public static final int THIRD_HINT_PURCHASE_POINT = 30;
    public static final Long SSE_TIMEOUT = 1000L * 60L;
    public static final String DEFAULT_GROUP_IMAGE_URL = GROUP_IMAGE_FOLDER + "/default.png";
    public static final String DEFAULT_PROFILE_BACKGROUND_IMAGE_URL = PROFILE_BG_IMAGE_FOLRDER + "/default.png";
    public static final String DEFAULT_PROFILE_IMAGE_FILENAME = PROFILE_BG_IMAGE_FOLRDER + "default.jpeg";

    private Constants() {
    }
}