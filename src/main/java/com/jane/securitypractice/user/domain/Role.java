package com.jane.securitypractice.user.domain;

public enum Role {
    USER, ADMIN;

    public String getKey() {
        return "ROLE_" + name(); // 앞에 ROLE_ 을 붙여야 Spring Security 권한 인식
    }
}
