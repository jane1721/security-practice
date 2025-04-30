package com.jane.securitypractice.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER) // 권한은 거의 항상 함께 쓰이므로 User 조회 시 즉시 가져오도록 함
    @CollectionTable(name = "user_roles")
    @Enumerated(EnumType.STRING) // Enum 이름 그대로 저장
    private List<Role> roles;
}
