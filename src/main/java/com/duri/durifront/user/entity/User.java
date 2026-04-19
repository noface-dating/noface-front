package com.duri.durifront.user.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class User {

    @Id
    @Column(name = "user_id", length = 36)
    @GeneratedValue
    @UuidGenerator
    private String userId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "platform_type", nullable = false, length = 20)
    @Enumerated(value = STRING)
    private PlatformType platformType;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(value = STRING)
    private UserRole role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public static User createUser(String username,
                                  String password,
                                  String email)
    {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPlatformType(PlatformType.LOCAL);
        user.setRole(UserRole.USER);

        return user;
    }

    // TODO: 마지막 로그인 일시 저장 적용
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

}