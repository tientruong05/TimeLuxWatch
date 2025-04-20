package com.TimeLuxWatchBE.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.repository.UserRepository;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/changepass")
public class ChangePassApiController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/changePassword")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        return Optional.ofNullable((UserEntity) session.getAttribute("user"))
                .map(user -> {
                    // Kiểm tra mật khẩu hiện tại
                    String encodedCurrentPassword = Base64.getEncoder().encodeToString(currentPassword.getBytes());
                    if (!user.getPassword().equals(encodedCurrentPassword)) {
                        response.put("error", "Mật khẩu hiện tại không đúng!");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // Kiểm tra mật khẩu mới và xác nhận có khớp không
                    if (!newPassword.equals(confirmPassword)) {
                        response.put("error", "Mật khẩu mới và xác nhận không khớp!");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // Kiểm tra độ dài mật khẩu mới
                    if (newPassword.length() < 6) {
                        response.put("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // Kiểm tra mật khẩu mới có trùng với mật khẩu cũ không
                    String encodedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());
                    if (encodedNewPassword.equals(user.getPassword())) {
                        response.put("error", "Mật khẩu mới không được trùng với mật khẩu cũ!");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // Kiểm tra mật khẩu mới có chứa 6 dấu cách liên tiếp không
                    if (containsSixSpaces(newPassword)) {
                        response.put("error", "Mật khẩu mới không được chứa 6 dấu cách liên tiếp!");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // Cập nhật mật khẩu mới
                    user.setPassword(encodedNewPassword);
                    userRepository.save(user);
                    session.setAttribute("user", user);

                    response.put("message", "Đổi mật khẩu thành công!");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    // Phương thức kiểm tra 6 dấu cách liên tiếp
    private boolean containsSixSpaces(String password) {
        return Pattern.compile("\\s{6,}").matcher(password).find();
    }
} 