package com.example.nhom3.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhom3.user.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Tìm user theo username (Dùng khi đăng nhập)
    Optional<User> findByUsername(String username);

    // Tìm user theo email (Dùng khi quên mật khẩu)
    Optional<User> findByEmail(String email);

    // Tìm user theo token reset mật khẩu
    Optional<User> findByResetPasswordToken(String resetPasswordToken);

    // Kiểm tra tồn tại (Dùng khi đăng ký)
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}