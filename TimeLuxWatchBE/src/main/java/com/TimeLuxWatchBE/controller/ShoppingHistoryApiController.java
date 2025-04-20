package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.entity.OrderDetailEntity;
import com.TimeLuxWatchBE.entity.OrderEntity;
import com.TimeLuxWatchBE.service.OrderDetailService;
import com.TimeLuxWatchBE.service.OrderService;
import com.TimeLuxWatchBE.dto.ShoppingHistoryItemDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopping-history")
public class ShoppingHistoryApiController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getShoppingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = true) Integer userId) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        try {
            Page<ShoppingHistoryItemDTO> historyPage = orderDetailService.findHistoryByUserId(userId, pageable);

            response.put("orderDetails", historyPage.getContent());
            response.put("currentPage", historyPage.getNumber());
            response.put("totalPages", historyPage.getTotalPages());
            response.put("totalItems", historyPage.getTotalElements());
            response.put("pageSize", historyPage.getSize());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error fetching shopping history for user " + userId + ": " + e.getMessage());
            e.printStackTrace();

            response.put("error", "Có lỗi xảy ra khi lấy lịch sử mua sắm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 