package com.TimeLuxWatchBE.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingHistoryItemDTO {
    private int id; // OrderDetail ID
    private ProductSummaryDTO product;
    private int qty;
    private Date orderDate;
} 