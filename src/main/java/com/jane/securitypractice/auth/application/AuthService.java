package com.jane.securitypractice.auth.application;

import com.jane.securitypractice.auth.dto.UserRegisterDto;
import com.jane.securitypractice.user.domain.User;
import com.jane.securitypractice.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isUsernameDuplicate(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void registerUser(UserRegisterDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(List.of(userDto.getSelectedRole()))
                .build();

        userRepository.save(user);
    }
}
