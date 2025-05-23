package com.jane.securitypractice.auth.config;

import com.jane.securitypractice.user.domain.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout") // 기본값
                    .logoutSuccessUrl("/login?logout=true") // 로그아웃 후 리디렉션할 주소
                    .invalidateHttpSession(true) // 세션 무효화
                    .clearAuthentication(true) // 인증 정보 삭제
                    .deleteCookies("JSESSIONID") // 세션 쿠키 제거
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/register", "/access-denied", "/session-expired").permitAll()
                    .requestMatchers("/h2-console/**").permitAll() // h2-console 은 누구나 접속 허용
                    .requestMatchers("/user/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers("/admin/**").hasRole(Role.ADMIN.name()) // 내부적으로 ROLE_ADMIN 과 비교
                    .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                    .accessDeniedPage("/access-denied")
            )
            .rememberMe(remember -> remember
                    .key("my-unique-key") // 고유 키 지정
                    .tokenValiditySeconds(7 * 24 * 60 * 60) // 7일 유지
                    .tokenRepository(persistentTokenRepository())
            )
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**") // h2-console 은 CSRF 토큰 검사 안 함
            )
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin()) // h2-console iframe 허용
            )
            .sessionManagement(session -> session
                    /**
                     * SessionCreationPolicy
                     * - ALWAYS : 요청마다 항상 세션 생성
                     * - IF_REQUIRED : 필요할 때만 세션 생성 (기본값)
                     * - NEVER : Spring Security 는 세션을 생성하지 않음. 다만, 이미 있는 세션은 사용
                     * - STATELESS : 세션을 생성도, 사용도 하지 않음 (JWT 등 토큰 인증 시 사용)
                     */
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 기본값
                    .maximumSessions(-1) // 최대 허용 세션 수 (-1: 무제한)
                    .maxSessionsPreventsLogin(false) // true: 새 로그인 거부, false: 이전 세션 만료 (기본값)
                    .sessionRegistry(sessionRegistry()) // Spring Security 내부적으로 SessionRegistry 를 통해 세션을 추적
                    .expiredUrl("/login")
                    .expiredSessionStrategy(event -> {
                        // 세션 만료 시 추가 처리 기능
                        log.info("세션 만료: " + event.getSessionInformation().getPrincipal());
                        HttpServletResponse response = event.getResponse();
                        response.sendRedirect("/session-expired");
                    })
            );
        return http.build();
    }

    // DB 기반 persistent token repository
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    // Spring Security 내부적으로 SessionRegistry 를 통해 세션을 추적
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl(); // 내부적으로 ConcurrentMap 을 사용하여 세션-유저 매핑을 관리
    }

    // 세션 종료 이벤트 감지용 - 없으면 로그아웃 시 세션 제거가 안됨
    @Bean
    public static HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
