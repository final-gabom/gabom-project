package com.explorer.gabom.domain.point.entity;

import com.explorer.gabom.domain.point.type.PointType;
import com.explorer.gabom.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "point_log")
@Getter
public class PointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType pointType;

    @Column(nullable = false)
    private int point;

    @CreatedDate
    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;
}
