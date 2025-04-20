package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.repository.CategoryRepository;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/brands")
public class BrandApiController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<BrandDTO>> getBrands() {
        // Return a simplified list of categories with ID and name only
        List<BrandDTO> brands = categoryRepository.findAll().stream()
            .map(category -> new BrandDTO(category.getId(), category.getName()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(brands);
    }
    
    @Data
    @AllArgsConstructor
    public static class BrandDTO {
        private int id;
        private String name;
    }
} 