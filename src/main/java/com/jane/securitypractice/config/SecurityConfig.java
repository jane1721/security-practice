package com.jane.securitypractice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

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
                    .requestMatchers("/login", "/register").permitAll()
                    .anyRequest().authenticated()
            );
        return http.build();
    }
}
