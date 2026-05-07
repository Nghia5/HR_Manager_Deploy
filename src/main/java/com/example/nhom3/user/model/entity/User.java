package com.example.nhom3.user.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "reset_password_expires")
    private LocalDateTime resetPasswordExpires;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at") // hoặc "delete_at" tùy bạn chọn chạy lệnh nào ở trên
    private LocalDateTime deletedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    // Tự động gán thời gian tạo khi insert mới
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true; // Default là 1 (hoạt động)
        }
    }

    // Tự động gán thời gian cập nhật khi update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
