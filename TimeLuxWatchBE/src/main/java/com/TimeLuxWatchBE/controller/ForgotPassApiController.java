package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.repository.UserRepository;
import com.TimeLuxWatchBE.service.MailService;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/forgotpass")
public class ForgotPassApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam String username,
            @RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = userRepository.findByUsernameAndEmail(username, email);
        if (user != null) {
            String newPassword = generateValidPassword();
            String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
            user.setPassword(encodedNewPassword);
            userRepository.save(user);

            String emailContent = "Mật khẩu mới của bạn là: " + newPassword;
            try {
                mailService.sendEmail(user.getEmail(), "Khôi phục mật khẩu", emailContent);
                response.put("message", "Mật khẩu mới đã được gửi đến email của bạn.");
            } catch (Exception e) {
                response.put("message", "Mật khẩu đã được đặt lại, nhưng không thể gửi email xác nhận.");
            }

            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Không tìm thấy tài khoản với thông tin này.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String generateValidPassword() {
        String password = generateRandomPassword();
        if (containsSixSpaces(password)) {
            return generateValidPassword(); // Đệ quy nếu không hợp lệ
        }
        return password;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private boolean containsSixSpaces(String password) {
        return Pattern.compile("\\s{6,}").matcher(password).find();
    }
} 