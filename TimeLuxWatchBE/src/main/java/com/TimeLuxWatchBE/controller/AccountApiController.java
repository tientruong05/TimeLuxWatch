package com.TimeLuxWatchBE.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.repository.UserRepository;
import com.TimeLuxWatchBE.entity.UserEntity;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AccountApiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuthentication(
            HttpSession session, 
            HttpServletRequest request) {
        Map<String, Object> response_map = new HashMap<>();
        
        System.out.println("--- Check Auth Start (Using SecurityContext) ---");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = null;
        boolean isAuthenticated = false;

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            isAuthenticated = true;
            Object principal = authentication.getPrincipal();
            String userEmail = null;
            
            if (principal instanceof UserDetails) {
                userEmail = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                userEmail = (String) principal;
            }
            
            if (userEmail != null) {
                user = userRepository.findByEmail(userEmail);
                System.out.println("User found via SecurityContext: " + userEmail);
            } else {
                System.out.println("Principal is not UserDetails or String: " + principal.getClass());
                 isAuthenticated = false; // Consider this not authenticated properly
            }
        } else {
            System.out.println("No valid or authenticated Authentication object found in SecurityContext.");
            if (authentication != null) {
                System.out.println("Authentication present but not authenticated or is Anonymous. Type: " + authentication.getClass().getName() + ", Is Authenticated: " + authentication.isAuthenticated());
            }
        }

        if (isAuthenticated && user != null) {
            response_map.put("authenticated", true);
            response_map.put("username", user.getUsername());
            response_map.put("role", user.isRole());
            response_map.put("user", user);
            Object cartCountObj = session.getAttribute("cartCount");
            if (cartCountObj instanceof Integer) {
                response_map.put("cartCount", (Integer) cartCountObj);
            }
            System.out.println("--- Check Auth End - Authenticated (200 OK) via SecurityContext ---");
            return ResponseEntity.ok(response_map);
        } else {
            System.out.println("User *not* authenticated according to SecurityContext or DB lookup failed.");
            response_map.put("authenticated", false);
            response_map.put("message", "User not authenticated or session invalid/expired");
            System.out.println("--- Check Auth End - Not Authenticated (401) via SecurityContext ---");
            return ResponseEntity.status(401).body(response_map);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> viewProfile(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("--- Profile Request Start (Using SecurityContext) ---");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = null;

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
             String userEmail = null;
            if (principal instanceof UserDetails) {
                userEmail = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                userEmail = (String) principal;
            }
            
            if (userEmail != null) {
                user = userRepository.findByEmail(userEmail);
                System.out.println("Profile - User found via SecurityContext: " + userEmail);
            } else {
                 System.out.println("Profile - Principal is not UserDetails or String: " + principal.getClass());
            }
        }
         else {
             System.out.println("Profile - No valid Authentication found in SecurityContextHolder.");
        }
        
        if (user != null) {
            response.put("user", user);
            System.out.println("--- Profile Request End - Success (200 OK) ---");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Profile - User *not* authenticated or found via SecurityContext.");
            response.put("error", "Không tìm thấy thông tin người dùng hoặc chưa đăng nhập.");
            System.out.println("--- Profile Request End - Unauthorized (401) ---");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("fullname") String fullname,
            @RequestParam("phonenumber") String phoneNumber,
            @RequestParam String address,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
             Object principal = authentication.getPrincipal();
             String userEmail = null;
            if (principal instanceof UserDetails) userEmail = ((UserDetails) principal).getUsername();
            else if (principal instanceof String) userEmail = (String) principal;
            
            if(userEmail != null) user = userRepository.findByEmail(userEmail);
        }

        if (user == null) {
            response.put("error", "Không tìm thấy thông tin người dùng hoặc chưa đăng nhập.");
            return ResponseEntity.status(401).body(response);
        }
        
        // Validate input
        if (!fullname.matches("^[^0-9]+$") || fullname.trim().isEmpty()) {
            response.put("error", "Họ và tên không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
        if (!phoneNumber.matches("^\\d{10}$")) {
            response.put("error", "Số điện thoại phải có đúng 10 chữ số!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            user.setFullName(fullname);
            user.setPhone(phoneNumber);
            user.setAddress(address);
            userRepository.save(user);
            
            response.put("message", "Cập nhật thông tin thành công!");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Có lỗi xảy ra khi cập nhật: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Thêm endpoint debug session
    @GetMapping("/debug-session")
    public ResponseEntity<Map<String, Object>> debugSession(HttpSession session, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("sessionId", session.getId());
        response.put("sessionCreationTime", session.getCreationTime());
        response.put("sessionMaxInactiveInterval", session.getMaxInactiveInterval());
        response.put("sessionNew", session.isNew());
        
        // Danh sách attribute trong session
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            attributes.put(name, session.getAttribute(name));
        }
        response.put("sessionAttributes", attributes);
        
        // Headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        response.put("headers", headers);
        
        // Cookies
        Map<String, String> cookies = new HashMap<>();
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }
        response.put("cookies", cookies);
        
        return ResponseEntity.ok(response);
    }
} 