package com.jane.securitypractice.auth.config;

import com.jane.securitypractice.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

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
                    .requestMatchers("/login", "/register", "/access-denied").permitAll()
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
}
