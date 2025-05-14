package com.jane.securitypractice.user.domain;

import com.jane.securitypractice.auth.infrastructure.CustomUserDetails;
import com.jane.securitypractice.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
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

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id);
        return new UserDto(user.getId(), user.getUsername(), user.getRoles());
    }

    @Transactional // 트랜잭션 종료 시점에 Dirty Checking 으로 update SQL 이 실행
    public void updateUserRoles(Long id, Set<Role> roles) {
        User user = userRepository.findById(id);
        user.updateRoles(roles); // 내부적으로 Spring Data JPA 의 Repository 을 사용하고 있으므로 영속성 컨텍스트 관리 대상
    }

    @Transactional
    public void updateUserAboutMe(Authentication authentication, String username, String aboutMe) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.updateAboutMe(aboutMe);

        CustomUserDetails updatedPrincipal = new CustomUserDetails(user);

        // 변경된 정보로 SecurityContext 세션 정보 갱신
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedPrincipal,
                authentication.getCredentials(),
                updatedPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
