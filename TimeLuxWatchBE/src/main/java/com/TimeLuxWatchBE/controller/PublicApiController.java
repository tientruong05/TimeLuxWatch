package com.TimeLuxWatchBE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testPublicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Public API is working!");
        response.put("timestamp", LocalDateTime.now().toString());
        
        // Thêm dữ liệu mẫu để test
        List<Map<String, Object>> sampleProducts = new ArrayList<>();
        
        for (int i = 1; i <= 4; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", i);
            product.put("name", "Đồng hồ mẫu " + i);
            product.put("price", 1000000 * i);
            product.put("discountedPrice", 800000 * i);
            product.put("discountPercentage", 20);
            product.put("image", "watch" + i + ".jpg");
            sampleProducts.add(product);
        }
        
        response.put("sampleProducts", sampleProducts);
        
        return ResponseEntity.ok(response);
    }
} 