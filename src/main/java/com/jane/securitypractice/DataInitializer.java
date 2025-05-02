package com.jane.securitypractice;

import com.jane.securitypractice.user.domain.Role;
import com.jane.securitypractice.user.domain.User;
import com.jane.securitypractice.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            userRepository.save(User.builder()
                .username("user1")
                .password(passwordEncoder.encode("pass1234"))
                .roles(Set.of(Role.USER))
                .build());
        };
    }
}
