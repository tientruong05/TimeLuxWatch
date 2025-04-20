package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.config.FlashSaleManager;
import com.TimeLuxWatchBE.dto.HomePageDataDTO;
import com.TimeLuxWatchBE.dto.HomePageProductDTO;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.service.FlashSaleService;
import com.TimeLuxWatchBE.service.ProductService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private FlashSaleService flashSaleService;

    @GetMapping("/homepage")
    public ResponseEntity<HomePageDataDTO> getHomepageData() {
        HomePageDataDTO responseDTO = new HomePageDataDTO();
        
        // Get new products using DTO method
        List<HomePageProductDTO> newProducts = productService.getNewProductsDTO();
        responseDTO.setNewProducts(newProducts);
        
        // Get best selling products using DTO method
        List<HomePageProductDTO> bestProducts = productService.getBestProductsDTO();
        responseDTO.setBestProducts(bestProducts);
        
        // Get sale products using DTO method
        List<HomePageProductDTO> saleProducts = productService.getSaleProductsDTO();
        responseDTO.setSaleProducts(saleProducts);
        
        // Check and get Flash Sale information
        boolean isFlashSaleActive = flashSaleService.isFlashSaleActive();
        responseDTO.setFlashSaleActive(isFlashSaleActive);
        
        if (isFlashSaleActive) {
            // Get end time for countdown
            LocalDateTime flashSaleEndTime = flashSaleService.getFlashSaleEndTime();
            if (flashSaleEndTime != null) {
                responseDTO.setFlashSaleEndTime(flashSaleEndTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            // Get flash sale products - Assuming FlashSaleService returns needed data
            // Adapt this based on what getFlashSaleProductsForHomepage returns
            // If it returns ProductDTO, map it to HomePageProductDTO
            List<ProductDTO> flashSaleProductDTOs = flashSaleService.getFlashSaleProductsForHomepage(); 
            List<HomePageProductDTO> flashSaleHomePageDTOs = flashSaleProductDTOs.stream()
                .map(p -> new HomePageProductDTO(
                    (long) p.getId(), 
                    p.getName(), 
                    p.getImage(), 
                    (p.getPrice() != 0.0f) ? BigDecimal.valueOf(p.getPrice()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    (p.getDiscountedPrice() != null) ? BigDecimal.valueOf(p.getDiscountedPrice()).setScale(2, RoundingMode.HALF_UP) : null,
                    (p.getDiscountPercentage() != null) ? p.getDiscountPercentage().doubleValue() : null
                ))
                .collect(Collectors.toList());
            responseDTO.setFlashSaleProducts(flashSaleHomePageDTOs);
            
            // Get discount name for display
            flashSaleService.getCurrentFlashSale().ifPresent(discount -> {
                responseDTO.setFlashSaleName(discount.getDiscountName());
            });
        }

        return ResponseEntity.ok(responseDTO);
    }

    // Thêm endpoint test đơn giản để kiểm tra CORS
    @GetMapping("/test-cors")
    public ResponseEntity<Map<String, String>> testCors() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS is working correctly!");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam("q") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> products = productService.findSearchAll(search, pageable);
        List<HomePageProductDTO> productDTOs = products.getContent().stream()
                                                    .map(HomePageProductDTO::new)
                                                    .collect(Collectors.toList());

        response.put("currentPage", products.getNumber());
        response.put("totalPages", products.getTotalPages());
        response.put("pageSize", products.getSize());
        response.put("totalItems", products.getTotalElements());
        response.put("products", productDTOs);
        response.put("search", search);

        return ResponseEntity.ok(response);
    }
} 