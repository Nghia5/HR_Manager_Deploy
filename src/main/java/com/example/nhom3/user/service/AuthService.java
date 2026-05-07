package com.example.nhom3.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.nhom3.user.model.dto.JwtAuthResponse;
import com.example.nhom3.user.model.dto.LoginRequest;
import com.example.nhom3.user.model.dto.RegisterRequest;
import com.example.nhom3.user.model.entity.User;
import com.example.nhom3.user.repository.UserRepository;
import com.example.nhom3.user.security.JwtTokenProvider;

import jakarta.mail.internet.MimeMessage;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired // Đã bổ sung @Autowired để tránh lỗi NullPointerException
    private JwtTokenProvider jwtTokenProvider;

    // Logic Đăng ký
    public String register(RegisterRequest registerDto) {
        if (userRepository.existsByUsername(registerDto.username())) {
            throw new RuntimeException("Lỗi: Username đã được sử dụng!");
        }
        if (userRepository.existsByEmail(registerDto.email())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(registerDto.username());
        user.setEmail(registerDto.email());
        user.setPasswordHash(passwordEncoder.encode(registerDto.password()));

        userRepository.save(user);

        return "Đăng ký tài khoản thành công!";
    }

    // Logic Đăng nhập
    public JwtAuthResponse login(LoginRequest loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return new JwtAuthResponse(token, loginDto.username());
    }

    // Logic Yêu cầu quên mật khẩu
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Email không tồn tại trong hệ thống!"));

        // Tạo Token ngẫu nhiên (UUID)
        String token = UUID.randomUUID().toString();

        // Lưu Token và thời gian hết hạn (15 phút sau)
        user.setResetPasswordToken(token);
        user.setResetPasswordExpires(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Gọi hàm gửi Email
        sendEmail(email, token);

        return "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư!";
    }

    // Logic Xác nhận đặt lại mật khẩu mới
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Lỗi: Mã Token không hợp lệ!"));

        // Kiểm tra Token đã hết hạn chưa
        if (user.getResetPasswordExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Lỗi: Mã Token đã hết hạn!");
        }

        // Cập nhật mật khẩu mới và xóa Token cũ
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        userRepository.save(user);

        return "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập bằng mật khẩu mới.";
    }

    // Hàm nội bộ hỗ trợ gửi Email
    private void sendEmail(String email, String token) {
        String resetLink = "http://127.0.0.1:5500/src/main/resources/templates/reset-password.html?token="
                + token;

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Yêu cầu đặt lại mật khẩu - HR Management");

            //
            String content = "<p>Chào bạn,</p>"
                    + "<p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link bên dưới:</p>"
                    + "<p><a href=\"" + resetLink + "\">Đặt lại mật khẩu của tôi</a></p>"
                    + "<br>"
                    + "<p>Link này sẽ hết hạn sau 15 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>";

            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi mail: " + e.getMessage());
        }
    }
}