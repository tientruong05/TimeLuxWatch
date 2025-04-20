package com.TimeLuxWatchBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private int id;
    private String name;
    private String image;
    private float price;
    private Float discountedPrice; 
    private Float discountPercentage;
    private int qty;
    private int sellProgress; 
    private int status; 
    private String description;
  
    private SubCategoryEntity subCategory; 
    private boolean isDiscounted; 

    public void setDiscounted(boolean isDiscounted) {
        this.isDiscounted = isDiscounted;
    }
}
