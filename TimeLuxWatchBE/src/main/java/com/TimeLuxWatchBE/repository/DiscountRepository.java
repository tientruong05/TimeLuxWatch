package com.TimeLuxWatchBE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.entity.DiscountEntity;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Integer> {
    @Query("SELECT d FROM DiscountEntity d WHERE d.status = 1 " +
           "AND d.startDate <= CURRENT_DATE " +
           "AND d.endDate >= CURRENT_DATE " +
           "ORDER BY d.discountValue DESC")
    List<DiscountEntity> findActiveDiscounts();
    
    default Optional<DiscountEntity> findActiveFlashSale() {
        List<DiscountEntity> discounts = findActiveDiscounts();
        return discounts.isEmpty() ? Optional.empty() : Optional.of(discounts.get(0));
    }
    
    @Query(value = "SELECT TOP 1 * FROM discounts d WHERE d.status = 1 " +
           "AND d.start_date <= CONVERT(DATE, GETDATE()) " +
           "AND d.end_date >= CONVERT(DATE, GETDATE()) " +
           "ORDER BY d.discount_value DESC", nativeQuery = true)
    Optional<DiscountEntity> findActiveFlashSaleNative();
    
    @Query("SELECT d FROM DiscountEntity d WHERE d.status = 1 " +
           "AND d.startDate <= CURRENT_DATE " +
           "AND d.endDate >= CURRENT_DATE")
    List<DiscountEntity> findAllActiveDiscounts();

    @Query("SELECT DISTINCT d FROM DiscountEntity d " +
           "JOIN d.discountDetails dd " +
           "WHERE d.status = 1 " +
           "AND d.startDate <= CURRENT_DATE " +
           "AND d.endDate >= CURRENT_DATE " +
           "AND dd.status = 1 AND (" +
           "   (dd.product IS NOT NULL AND dd.product.id = :productId) OR " +
           "   (dd.category IS NOT NULL AND dd.category.id = :categoryId) OR " +
           "   (dd.subCategory IS NOT NULL AND dd.subCategory.id = :subCategoryId)" +
           ")")
    List<DiscountEntity> findAllActiveDiscountsForProduct(
        @Param("productId") int productId,
        @Param("categoryId") int categoryId,
        @Param("subCategoryId") int subCategoryId
    );
}
