package com.TimeLuxWatchBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.entity.CartEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    /**
     * Tìm tất cả các giỏ hàng theo ID người dùng
     * @param userId ID của người dùng
     * @return Danh sách các mục giỏ hàng
     */
    @Query("SELECT c FROM CartEntity c WHERE c.user.id = :userId")
    List<CartEntity> findByUserId(@Param("userId") int userId);
    
    /**
     * Tìm giỏ hàng theo ID người dùng và ID sản phẩm
     * @param userId ID của người dùng
     * @param productId ID của sản phẩm
     * @return Optional chứa giỏ hàng (nếu tồn tại)
     */
    @Query("SELECT c FROM CartEntity c WHERE c.user.id = :userId AND c.product.id = :productId")
    Optional<CartEntity> findByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);
} 
