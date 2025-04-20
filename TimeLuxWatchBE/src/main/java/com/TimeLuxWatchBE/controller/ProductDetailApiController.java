package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.dto.ProductDetailResponseDTO;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductDetailApiController {

    @Autowired
    private ProductService productService;

    @GetMapping("/detail/{id}")
    public ResponseEntity<ProductDetailResponseDTO> getProductDetail(@PathVariable("id") int id) {
        ProductDetailResponseDTO responseDTO = productService.getProductDetailAndRelated(id);

        if (responseDTO != null && responseDTO.getProduct() != null) {
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 