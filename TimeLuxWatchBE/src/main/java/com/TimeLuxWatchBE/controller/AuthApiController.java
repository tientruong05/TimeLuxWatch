package com.TimeLuxWatchBE.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.entity.CartEntity;
import com.TimeLuxWatchBE.repository.UserRepository;
import com.TimeLuxWatchBE.service.CartService;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private CartService cartService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(
            @RequestParam("username") String email,
            @RequestParam("password") String rawPassword,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(); 
        
        System.out.println("--- Login Request Start ---");
        System.out.println("Login - Session ID (before): " + session.getId() + ", isNew: " + session.isNew());

        try {
            UserEntity userEntity = userRepository.findByEmail(email);
            if (userEntity == null) {
                System.out.println("Login Failed: User not found with email: " + email);
                throw new BadCredentialsException("User not found");
            }
            if (!userEntity.isStatus()) {
                 System.out.println("Login Failed: User not activated: " + email);
                 throw new BadCredentialsException("User not activated");
            }

            String storedPasswordBase64 = userEntity.getPassword();
            String providedPasswordBase64 = Base64.getEncoder().encodeToString(rawPassword.getBytes());

            if (!storedPasswordBase64.equals(providedPasswordBase64)) {
                 System.out.println("Login Failed: Invalid password for user: " + email);
                 throw new BadCredentialsException("Invalid password");
            }
            
            System.out.println("Password match successful for user: " + email);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (userEntity.isRole()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                 authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userEntity.getEmail(),
                null,
                authorities
            );
            
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            if (!session.isNew()) {
                 System.out.println("Invalidating old session: " + session.getId());
                session.invalidate();
                session = request.getSession(true);
                System.out.println("Created new session after invalidation: " + session.getId());
            }
            
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            session.setAttribute("user", userEntity);

            int cartCount = cartService.getCartItemsByUserId(userEntity.getId()).stream()
                    .mapToInt(CartEntity::getQty).sum();
            session.setAttribute("cartCount", cartCount);

            System.out.println("Spring Security Context set for user: " + email + " (Session: " + session.getId() + ")");

            response.put("message", "Đăng nhập thành công!");
            response.put("user", userEntity); 
            response.put("cartCount", cartCount);

            ResponseCookie sessionCookie = ResponseCookie.from("JSESSIONID", session.getId())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(session.getMaxInactiveInterval())
                .sameSite("Lax") 
                .build();

            System.out.println("--- Login Request End - Success --- Sending response with Set-Cookie.");
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(response);

        } catch (BadCredentialsException e) {
            System.out.println("--- Login Request End - Failed (Bad Credentials) ---");
            SecurityContextHolder.clearContext(); 
            response.put("error", "Sai mật khẩu, tài khoản chưa kích hoạt hoặc email không tồn tại!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); 
        } catch (Exception e) {
            System.out.println("--- Login Request End - Failed (Other Error) --- Error: " + e.getMessage());
            e.printStackTrace(); 
            SecurityContextHolder.clearContext();
            response.put("error", "Lỗi đăng nhập không xác định.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String fullname) {
        Map<String, Object> response = new HashMap<>();
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            response.put("error", "Email không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
        UserEntity existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            response.put("error", "Email đã được sử dụng!");
            return ResponseEntity.badRequest().body(response);
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setFullName(fullname);
        newUser.setEmail(email);
        newUser.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
        newUser.setPhone(phone == null || phone.trim().isEmpty() ? "N/A" : phone);
        newUser.setAddress(address);
        newUser.setRole(false);
        newUser.setStatus(false);

        userRepository.save(newUser);
        sendActivationEmail(newUser);
        response.put("message", "Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
        return ResponseEntity.ok(response);
    }

    private void sendActivationEmail(UserEntity user) {
        String activationLink = "http://localhost:8080/api/auth/activate?email=" + user.getEmail();
        String subject = "Chào mừng bạn đến với TimeLux Watch - Xác nhận tài khoản";
        String htmlContent =
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<div style='padding: 20px; background-color: #ffffff; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1a1a1a; text-align: center;'>Xác Nhận Tài Khoản</h2>" +
                    "<p style='color: #333333;'>Kính gửi " + user.getFullName() + ",</p>" +
                    "<p style='color: #333333;'>Chào mừng bạn đến với TimeLux Watch - Nơi thời gian được tôn vinh!</p>" +
                    "<p style='color: #333333;'>Chúng tôi vô cùng hân hạnh được đồng hành cùng bạn trên hành trình khám phá những kiệt tác đồng hồ xa xỉ.</p>" +
                    "<div style='text-align: center; margin: 30px 0;'>" +
                        "<a href='" + activationLink + "' style='background-color: #1a1a1a; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>XÁC NHẬN TÀI KHOẢN</a>" +
                    "</div>" +
                    "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0;'>" +
                        "<p style='color: #1a1a1a; font-weight: bold;'>Tại TimeLux Watch, chúng tôi cam kết mang đến cho bạn:</p>" +
                        "<ul style='color: #333333;'>" +
                            "<li>Bộ sưu tập đồng hồ cao cấp từ các thương hiệu danh tiếng</li>" +
                            "<li>Dịch vụ chăm sóc khách hàng đẳng cấp 5 sao</li>" +
                            "<li>Chế độ bảo hành và hậu mãi ưu việt</li>" +
                        "</ul>" +
                    "</div>" +
                    "<p style='color: #666666;'>Nếu bạn cần hỗ trợ, đừng ngần ngại liên hệ với chúng tôi qua hotline: <span style='color: #1a1a1a; font-weight: bold;'>093.934.8888</span></p>" +
                "</div>" +
                "<div style='text-align: center; padding: 20px; color: #666666;'>" +
                    "<p style='margin: 5px 0;'><strong>TimeLux Watch</strong></p>" +
                    "<p style='margin: 5px 0;'>Luxury Timepieces & Exceptional Service</p>" +
                    "<p style='margin: 5px 0;'>Website: www.timeluxwatch.com</p>" +
                    "<p style='margin: 5px 0;'>Email: support@timeluxwatch.com</p>" +
                "</div>" +
            "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<Map<String, Object>> activateAccount(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            user.setStatus(true);
            userRepository.save(user);
            response.put("message", "Tài khoản đã được kích hoạt thành công! Bạn có thể đăng nhập ngay.");
            return ResponseEntity.ok(response);
        }
        response.put("error", "Liên kết kích hoạt không hợp lệ!");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        System.out.println("--- Logout Request Start ---");
        SecurityContextHolder.clearContext();

        if (session != null) {
            System.out.println("Logout - Invalidating Session ID: " + session.getId());
            session.invalidate(); 
            response.put("message", "Đăng xuất thành công!");
        } else {
            System.out.println("No active session found to logout.");
            response.put("message", "Không có phiên hoạt động để đăng xuất hoặc đã hết hạn.");
        }
        
        response.put("status", "success");
        
        ResponseCookie deleteCookie = ResponseCookie.from("JSESSIONID", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();

        System.out.println("--- Logout Request End --- Sending delete cookie.");
        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
            .body(response);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            String newPassword = generateRandomPassword();
            user.setPassword(Base64.getEncoder().encodeToString(newPassword.getBytes()));
            userRepository.save(user);
            sendPasswordResetEmail(user.getEmail(), newPassword);
            response.put("message", "Mật khẩu mới đã được gửi đến email của bạn!");
            return ResponseEntity.ok(response);
        }
        response.put("error", "Email không tồn tại trong hệ thống!");
        return ResponseEntity.badRequest().body(response);
    }

    private void sendPasswordResetEmail(String email, String newPassword) {
        String subject = "Mật Khẩu Mới Của Bạn - TimeLux Watch";
        String htmlContent =
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<div style='padding: 20px; background-color: #ffffff; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #1a1a1a; text-align: center;'>Mật Khẩu Mới Của Bạn</h2>" +
                    "<p style='color: #333333;'>Chào bạn,</p>" +
                    "<p style='color: #333333;'>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu từ bạn. Dưới đây là mật khẩu mới của bạn:</p>" +
                    "<div style='text-align: center; margin: 20px 0;'>" +
                        "<p style='font-size: 1.2rem; font-weight: bold; color: #d4af37;'>" + newPassword + "</p>" +
                    "</div>" +
                    "<p style='color: #333333;'>Vui lòng đăng nhập bằng mật khẩu này và đổi mật khẩu mới để bảo mật tài khoản của bạn.</p>" +
                    "<div style='text-align: center; margin: 30px 0;'>" +
                        "<a href='http://localhost:8080/api/auth/login' style='background-color: #1a1a1a; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>ĐĂNG NHẬP NGAY</a>" +
                    "</div>" +
                    "<p style='color: #666666;'>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng liên hệ với chúng tôi ngay qua hotline: <span style='color: #1a1a1a; font-weight: bold;'>093.934.8888</span></p>" +
                "</div>" +
                "<div style='text-align: center; padding: 20px; color: #666666;'>" +
                    "<p style='margin: 5px 0;'><strong>TimeLux Watch</strong></p>" +
                    "<p style='margin: 5px 0;'>Luxury Timepieces & Exceptional Service</p>" +
                    "<p style='margin: 5px 0;'>Website: www.timeluxwatch.com</p>" +
                    "<p style='margin: 5px 0;'>Email: support@timeluxwatch.com</p>" +
                "</div>" +
            "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomPassword() {
        return String.format("%06d", new Random().nextInt(999999));
    }
} 