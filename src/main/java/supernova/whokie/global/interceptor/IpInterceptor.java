package supernova.whokie.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class IpInterceptor implements HandlerInterceptor {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String UNKNOWN = "unknown";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (ip != null && !UNKNOWN.equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0];
        } else {
            ip = request.getRemoteAddr();
        }
        request.setAttribute("visitorIp", ip);
        return true;
    }
}
