package com.TimeLuxWatchBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String image;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Double discountPercentage;
    private Integer qty;
    private String description;
    // Add category/subcategory names if needed by the detail view
    // private String subCategoryName;
    // private String categoryName;

    // Constructor mapping from ProductEntity
    public ProductDetailDTO(com.TimeLuxWatchBE.entity.ProductEntity entity) {
        this.id = (long) entity.getId();
        this.name = entity.getName();
        this.image = entity.getImage();
        this.price = BigDecimal.valueOf(entity.getPrice()).setScale(2, RoundingMode.HALF_UP);
        this.discountedPrice = BigDecimal.valueOf(entity.getDiscountedPrice()).setScale(2, RoundingMode.HALF_UP);
        Float percentage = entity.getDiscountPercentage();
        this.discountPercentage = (percentage != null) ? percentage.doubleValue() : null;
        this.qty = entity.getQty();
        this.description = entity.getDescription();

        // Example: Populate category/subcategory names if needed
        // if (entity.getSubCategory() != null) {
        //     this.subCategoryName = entity.getSubCategory().getSubCategoriesName();
        //     if (entity.getSubCategory().getCategory() != null) {
        //         this.categoryName = entity.getSubCategory().getCategory().getName();
        //     }
        // }
    }
} 