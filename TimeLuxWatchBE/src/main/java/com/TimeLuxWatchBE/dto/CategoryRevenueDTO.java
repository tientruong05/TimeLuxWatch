package com.TimeLuxWatchBE.dto;

import lombok.Data;

@Data
public class CategoryRevenueDTO {
    private String categoryName;
    private double totalRevenue;
    private int totalQty;
    private double maxPrice;
    private double minPrice;
    private double avgPrice;

    public CategoryRevenueDTO(String categoryName, double totalRevenue, int totalQty, double maxPrice, double minPrice, double avgPrice) {
        this.categoryName = categoryName;
        this.totalRevenue = totalRevenue;
        this.totalQty = totalQty;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.avgPrice = avgPrice;
    }
}
