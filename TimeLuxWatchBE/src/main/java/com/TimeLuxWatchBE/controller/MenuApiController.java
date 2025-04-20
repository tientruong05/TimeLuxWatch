package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuApiController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();
        List<CategoryEntity> categories = categoryService.getCategoriesByStatus(1); // 1 = active
        response.put("categories", categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/watch-brands")
    public ResponseEntity<Map<String, Object>> getWatchBrands() {
        Map<String, Object> response = new HashMap<>();
        List<CategoryEntity> watchBrands = categoryService.getCategoriesByStatus(1); // 1 = active
        response.put("watchBrands", watchBrands);
        return ResponseEntity.ok(response);
    }
} 