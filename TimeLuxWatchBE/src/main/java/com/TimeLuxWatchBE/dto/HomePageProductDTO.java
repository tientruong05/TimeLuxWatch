package com.TimeLuxWatchBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode; // Import RoundingMode for BigDecimal conversion

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageProductDTO {
    private Long id; // Use Long for IDs from entities
    private String name;
    private String image;
    private BigDecimal price;
    private BigDecimal discountedPrice; 
    private Double discountPercentage; // Use Double for percentage if needed

    // Constructor mapping from ProductEntity with type conversions
    public HomePageProductDTO(com.TimeLuxWatchBE.entity.ProductEntity entity) {
        this.id = (long) entity.getId(); // Cast int to long
        this.name = entity.getName();
        this.image = entity.getImage(); // Assuming image is just the filename
        
        // Convert float to BigDecimal safely
        this.price = BigDecimal.valueOf(entity.getPrice()).setScale(2, RoundingMode.HALF_UP);
        this.discountedPrice = BigDecimal.valueOf(entity.getDiscountedPrice()).setScale(2, RoundingMode.HALF_UP);

        // Convert Float to Double safely
        Float percentage = entity.getDiscountPercentage();
        this.discountPercentage = (percentage != null) ? percentage.doubleValue() : null;
    }
} 