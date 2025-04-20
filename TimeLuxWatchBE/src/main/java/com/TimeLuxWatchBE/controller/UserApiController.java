package com.TimeLuxWatchBE.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.service.CartService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user != null) {
            response.put("user", user);
            
            // Get cart count from session or recalculate it
            Integer cartCount = (Integer) session.getAttribute("cartCount");
            if (cartCount == null) {
                cartCount = cartService.getCartItemsByUserId(user.getId()).stream()
                    .mapToInt(cart -> cart.getQty())
                    .sum();
                session.setAttribute("cartCount", cartCount);
            }
            
            response.put("cartCount", cartCount);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No user logged in");
            return ResponseEntity.ok(response); // Return 200 with empty user to avoid frontend errors
        }
    }
} 