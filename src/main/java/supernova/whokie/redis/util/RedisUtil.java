package supernova.whokie.redis.util;

public final class RedisUtil {
    private static final String VISITOR_ID_FORMAT = "%d:%s";

    private RedisUtil() {}

    public static String generateVisitorId(Long hostId, String visitorIp) {
        return String.format(VISITOR_ID_FORMAT, hostId, visitorIp);
    }
}
