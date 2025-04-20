package com.TimeLuxWatchBE.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private int id;
    private String fullName;    
    private String address;   
    private String phone;     
    private double totalAmount; 
    private Date orderDate;      
    private long orderDateInMillis;
    private List<OrderDetailDTO> orderDetails;

    public OrderDTO(int id, String fullName, String address, String phone, double totalAmount, Date orderDate) {
        this.id = id;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderDateInMillis = orderDate != null ? orderDate.getTime() : 0;
    }
}
