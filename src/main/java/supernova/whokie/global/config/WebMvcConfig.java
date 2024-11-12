package supernova.whokie.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.interceptor.AdminInterceptor;
import supernova.whokie.global.interceptor.JwtInterceptor;
import supernova.whokie.global.interceptor.VisitorInterceptor;
import supernova.whokie.global.resolver.LoginUserArgumentResolver;
import supernova.whokie.global.resolver.TempUserArgumentResolver;
import supernova.whokie.global.resolver.VisitorArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;

    @Bean
    @Order(1)
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor(jwtProvider);
    }

    @Bean
    @Order(2)
    public VisitorInterceptor visitorInterceptor() {
        return new VisitorInterceptor();
    }

    @Bean
    @Order(3)
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor(jwtProvider);
    }

    @Bean
    public LoginUserArgumentResolver loginUserArgumentResolver() {
        return new LoginUserArgumentResolver();
    }

    @Bean
    public VisitorArgumentResolver visitorArgumentResolver() {
        return new VisitorArgumentResolver();
    }

    @Bean
    public TempUserArgumentResolver tempUserArgumentResolver() {
        return new TempUserArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/api/**");
        registry.addInterceptor(visitorInterceptor())
                .addPathPatterns("/api/profile/**");
        registry.addInterceptor(adminInterceptor())
                .addPathPatterns("/admin/**","/api/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver());
        resolvers.add(visitorArgumentResolver());
        resolvers.add(tempUserArgumentResolver());

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }
}
