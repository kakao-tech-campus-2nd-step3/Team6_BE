package supernova.whokie.global.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.exception.AuthenticationException;
import supernova.whokie.user.Role;

public class AdminInterceptor implements HandlerInterceptor {

    private static final String COOKIE_NAME = "jwt";
    private final JwtProvider jwtProvider; // JWT 처리 클래스 주입

    public AdminInterceptor(JwtProvider jwtTokenProvider) {
        this.jwtProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 쿠키에서 JWT 토큰 추출
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 토큰이 없는 경우
        if (token == null) {
            response.sendRedirect("/api/user/login"); // 로그인 페이지로 리다이렉트
            return false;
        }

        try {
            // JWT 토큰 검증 및 정보 추출
            Claims claims = jwtProvider.getClaim(token);
            Long userId = Long.parseLong(claims.getSubject());
            Role role = Role.valueOf(claims.get("role", String.class));


//             ADMIN 권한 체크
            if (!Role.ADMIN.equals(role)) {
                throw new AuthenticationException("관리자 권한이 필요합니다");
            }


            // request에 사용자 정보 설정
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

            return true;
        } catch (ExpiredJwtException e) {
            response.sendRedirect("/api/user/login"); // 토큰 만료
            return false;
        } catch (JwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }
}