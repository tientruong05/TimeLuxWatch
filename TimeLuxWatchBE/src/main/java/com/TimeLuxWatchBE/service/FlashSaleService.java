package com.TimeLuxWatchBE.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.entity.DiscountEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlashSaleService {
    // Kiểm tra nếu flash sale đang hoạt động
    boolean isFlashSaleActive();
    
    // Lấy thời gian kết thúc của flash sale
    LocalDateTime getFlashSaleEndTime();
    
    // Lấy tất cả sản phẩm đang trong flash sale
    List<ProductDTO> getFlashSaleProducts();
    
    // Lấy tất cả sản phẩm đang trong flash sale (có phân trang)
    Page<ProductDTO> getFlashSaleProductsPaged(Pageable pageable);
    
    // Lấy tối đa 4 sản phẩm flash sale cho trang chủ
    List<ProductDTO> getFlashSaleProductsForHomepage();
    
    // Lấy thông tin chi tiết flash sale
    Optional<DiscountEntity> getCurrentFlashSale();
    
    // Lấy tất cả các khuyến mãi đang hoạt động
    List<DiscountEntity> getAllActiveDiscounts();
    
    // Tính số lượng sản phẩm còn lại và phần trăm tiến trình bán
    int calculateSellProgress(int availableQuantity, int totalQuantity);
    
    // Kiểm tra khuyến mãi áp dụng cho sản phẩm cụ thể
    boolean isProductInFlashSale(int productId);
} 
