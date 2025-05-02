package com.jane.securitypractice.user.service;

import com.jane.securitypractice.user.dto.UserDto;
import com.jane.securitypractice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getRoles()))
                .collect(Collectors.toList());
    }
}
