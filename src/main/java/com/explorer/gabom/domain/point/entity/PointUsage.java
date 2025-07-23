package com.explorer.gabom.domain.point.entity;

import com.explorer.gabom.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "point_usage")
@Getter
public class PointUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long targetId;

    @Column(name = "used_point", nullable = false)
    private int usedPoint;

    @CreatedDate
    @Column(name = "useage_at", nullable = false, updatable = false)
    private LocalDateTime usageAt;

}
