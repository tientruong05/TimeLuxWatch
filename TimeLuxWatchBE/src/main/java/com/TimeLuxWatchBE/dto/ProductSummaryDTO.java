package com.TimeLuxWatchBE.dto;

// Using Lombok for boilerplate code (add dependency if needed)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Generates no-args constructor
@AllArgsConstructor // Generates all-args constructor
public class ProductSummaryDTO {
    private int id;
    private String name;
    private String image;
} 