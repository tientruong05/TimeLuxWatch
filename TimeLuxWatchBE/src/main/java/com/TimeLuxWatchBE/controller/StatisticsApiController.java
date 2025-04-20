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
import com.TimeLuxWatchBE.dto.CategoryRevenueDTO;
import com.TimeLuxWatchBE.dto.VipCustomerDTO;
import com.TimeLuxWatchBE.service.ProductService;
import com.TimeLuxWatchBE.service.VipCustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private VipCustomerService vipCustomerService;

    @GetMapping("/business")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryRevenueDTO> revenueStats = productService.getCategoryRevenue(pageable);

        response.put("revenueStats", revenueStats.getContent());
        response.put("currentPage", revenueStats.getNumber());
        response.put("totalPages", revenueStats.getTotalPages());
        response.put("pageSize", revenueStats.getSize());
        response.put("totalItems", revenueStats.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getVipCustomers(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "quarter", required = false) Integer quarter,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        Map<Integer, List<VipCustomerDTO>> vipCustomersByPeriod;
        Integer selectedYear = year;
        Integer selectedQuarter = quarter;

        if (year != null && quarter != null) {
            vipCustomersByPeriod = vipCustomerService.getTop10VipCustomersByQuarter(year, quarter, page, size);
        } else {
            vipCustomersByPeriod = vipCustomerService.getTop10VipCustomersByYear(year, page, size);
            if (vipCustomersByPeriod.isEmpty() && year == null) {
                selectedYear = vipCustomerService.getLatestYear();
                vipCustomersByPeriod = vipCustomerService.getTop10VipCustomersByYear(selectedYear, page, size);
            } else if (!vipCustomersByPeriod.isEmpty() && year == null) {
                selectedYear = vipCustomersByPeriod.keySet().iterator().next();
            }
        }

        response.put("vipCustomersByYear", vipCustomersByPeriod);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("selectedYear", selectedYear);
        response.put("selectedQuarter", selectedQuarter);

        return ResponseEntity.ok(response);
    }
} 