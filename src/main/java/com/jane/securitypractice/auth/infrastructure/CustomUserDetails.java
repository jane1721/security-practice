package com.jane.securitypractice.auth.infrastructure;

import com.jane.securitypractice.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 스프링 시큐리티의 UserDetails 인터페이스 구현
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // 객체 주소 비교가 아닌 CustomUserDetails 객체의 유저 이름이 동일한지 비교하기 위한 오버라이딩 메서드
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomUserDetails)) return false;
        CustomUserDetails other = (CustomUserDetails) obj;
        return this.getUsername().equals(other.getUsername());
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
    }
}
