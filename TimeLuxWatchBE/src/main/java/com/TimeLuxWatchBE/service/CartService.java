package com.TimeLuxWatchBE.service;

import com.TimeLuxWatchBE.entity.CartEntity;
import com.TimeLuxWatchBE.entity.UserEntity;
import com.TimeLuxWatchBE.dto.CartDetailDTO;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.entity.OrderEntity;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Optional;

public interface CartService {
    Optional<CartEntity> findById(int id); // Thêm nếu cần
    Optional<CartEntity> findByUserAndProduct(int userId, int productId);
    List<CartEntity> getCartItemsByUserId(int userId);
    void saveOrUpdate(CartEntity cart);
    void deleteCartItem(int id);
    
    // New business logic methods
    List<CartDetailDTO> getCartDetailsByUserId(int userId);
    String addItemToCart(UserEntity user, int productId, int quantity);
    String updateCartItemQuantity(int itemId, int quantity);
    int getCartCount(int userId);
    boolean validateStock(int productId, int quantity);
    List<CartDetailDTO> mapToCartDetailDTOs(List<CartEntity> cartItems);
    double calculateCartTotal(List<CartDetailDTO> cartItems);
    
    // Checkout related methods
    String validateCheckoutItems(List<CartDetailDTO> items);
    void processOrderItems(OrderEntity order, List<CartDetailDTO> items);
    
    // Email functionality
    void sendOrderConfirmationEmail(String email, OrderEntity order, List<CartDetailDTO> items, double total) throws MessagingException;
}
