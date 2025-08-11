package com.explorer.gabom.domain.auth.entity;


import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.oauth.type.OAuthProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "social_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerId"})
})
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 연관관계 편의 메서드 (User 설정)
    public void setUser(User user) {
        this.user = user;
    }
}
