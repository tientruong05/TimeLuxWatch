package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.service.FlashSaleService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/flash-sale")
public class FlashSaleApiController {

    @Autowired
    private FlashSaleService flashSaleService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getFlashSale(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        Map<String, Object> response = new HashMap<>();
        boolean isFlashSaleActive = flashSaleService.isFlashSaleActive();
        response.put("isFlashSaleActive", isFlashSaleActive);

        if (isFlashSaleActive) {
            // Định dạng thời gian kết thúc để JavaScript có thể xử lý
            LocalDateTime endTime = flashSaleService.getFlashSaleEndTime();
            response.put("flashSaleEndTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Lấy danh sách sản phẩm Flash Sale với phân trang
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDTO> flashSaleProductsPage = flashSaleService.getFlashSaleProductsPaged(pageable);

            response.put("flashSaleProducts", flashSaleProductsPage.getContent());
            response.put("currentPage", flashSaleProductsPage.getNumber());
            response.put("totalPages", flashSaleProductsPage.getTotalPages());
            response.put("totalItems", flashSaleProductsPage.getTotalElements());
            response.put("pageSize", size);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/homepage")
    public ResponseEntity<Map<String, Object>> getFlashSaleForHomepage() {
        Map<String, Object> response = new HashMap<>();
        boolean isFlashSaleActive = flashSaleService.isFlashSaleActive();
        response.put("isFlashSaleActive", isFlashSaleActive);

        if (isFlashSaleActive) {
            // Định dạng thời gian kết thúc để JavaScript có thể xử lý
            LocalDateTime endTime = flashSaleService.getFlashSaleEndTime();
            response.put("flashSaleEndTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Lấy tối đa 4 sản phẩm flash sale cho trang chủ
            response.put("flashSaleProducts", flashSaleService.getFlashSaleProductsForHomepage());
        }

        return ResponseEntity.ok(response);
    }
} 