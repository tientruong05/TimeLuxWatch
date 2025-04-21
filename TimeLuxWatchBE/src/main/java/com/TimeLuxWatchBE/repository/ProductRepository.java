package com.TimeLuxWatchBE.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.dto.CategoryRevenueDTO;
import com.TimeLuxWatchBE.entity.ProductEntity;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findBySubCategoryId(int subCategoryId);

    @Query(value = "SELECT p.* FROM products p ORDER BY p.qty DESC", nativeQuery = true)
    List<ProductEntity> bestProducts(Pageable pageable);

    @Query(value = "SELECT p.* FROM products p ORDER BY p.id DESC", nativeQuery = true)
    List<ProductEntity> newProducts(Pageable pageable);

    // Thay thế truy vấn saleProducts vì products không có cột discount
    @Query(value = "SELECT p.* FROM products p "
            + "JOIN discount_details dd ON p.id = dd.product_id "
            + "JOIN discounts d ON dd.discount_id = d.id "
            + "WHERE dd.status = 1 AND d.status = 1 "
            + "AND d.start_date <= CONVERT(DATE, GETDATE()) "
            + "AND d.end_date >= CONVERT(DATE, GETDATE()) "
            + "ORDER BY d.discount_value DESC", nativeQuery = true)
    List<ProductEntity> saleProducts(Pageable pageable);

    @Query(value = "SELECT \r\n"
            + "    c.name AS categoryName, \r\n"
            + "    COALESCE(SUM(od.qty * od.price), 0) AS totalRevenue, \r\n"
            + "    COALESCE(SUM(od.qty), 0) AS totalQty, \r\n"
            + "    COALESCE(MAX(od.price), 0) AS maxPrice, \r\n"
            + "    COALESCE(MIN(od.price), 0) AS minPrice, \r\n"
            + "    COALESCE(AVG(od.price), 0) AS avgPrice\r\n"
            + "FROM categories c\r\n"
            + "JOIN sub_categories sc ON c.id = sc.id_categories\r\n"
            + "JOIN products p ON sc.id = p.id_subcategories\r\n"
            + "JOIN order_detail od ON p.id = od.product_id\r\n"
            + "GROUP BY c.name\r\n"
            + "ORDER BY totalRevenue DESC",
            nativeQuery = true)
    Page<CategoryRevenueDTO> getCategoryRevenue(Pageable pageable);

    Page<ProductEntity> findByNameContainingIgnoreCaseOrSubCategory_Category_NameContainingIgnoreCaseOrSubCategory_SubCategoriesNameContainingIgnoreCase(String name, String categoryName, String subcategoryName, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.name LIKE CONCAT('%', ?1, '%') AND p.status = 1")
    Page<ProductEntity> findSearchAll(String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p " +
           "WHERE (:search IS NULL OR " +
           "       p.name LIKE CONCAT('%', :search, '%') OR " +
           "       p.subCategory.category.name LIKE CONCAT('%', :search, '%') OR " +
           "       p.subCategory.subCategoriesName LIKE CONCAT('%', :search, '%')) " +
           "AND (:categoryId IS NULL OR p.subCategory.category.id = :categoryId) " +
           "AND (:subCategoryId IS NULL OR p.subCategory.id = :subCategoryId) " +
           "AND (:status IS NULL OR p.status = :status)")
    Page<ProductEntity> findByFilters(
            @Param("search") String search,
            @Param("categoryId") Integer categoryId,
            @Param("subCategoryId") Integer subCategoryId,
            @Param("status") Integer status,
            Pageable pageable);

    // Truy vấn sản phẩm có discount đang hoạt động
    @Query("SELECT p FROM ProductEntity p " +
           "JOIN p.discountDetails dd " +
           "JOIN dd.discount d " +
           "WHERE d.status = 1 AND dd.status = 1 " +
           "AND d.startDate <= CURRENT_DATE AND d.endDate >= CURRENT_DATE")
    List<ProductEntity> findProductsWithActiveDiscounts(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.id IN :ids AND p.status = :status")
    List<ProductEntity> findByIdInAndStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.category.id IN :categoryIds AND p.status = :status")
    List<ProductEntity> findByCategoryIdsAndStatus(@Param("categoryIds") List<Integer> categoryIds, @Param("status") int status);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.id IN :subCategoryIds AND p.status = :status")
    List<ProductEntity> findBySubCategoryIdsAndStatus(@Param("subCategoryIds") List<Integer> subCategoryIds, @Param("status") int status);

    // Các phương thức bổ sung từ ProductDAO
    List<ProductEntity> findByNameContaining(String name);

    @Query("SELECT p FROM ProductEntity p WHERE p.status = 1")
    List<ProductEntity> findAllActive();

    @Query("SELECT p FROM ProductEntity p WHERE p.status = 1")
    Page<ProductEntity> findAllActive(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.status = 1 " +
           "AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%')) " +
           "AND (:gender IS NULL OR p.subCategory.subCategoriesName = :gender) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<ProductEntity> findAllWithFilters(
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.id = :subCategoryId AND p.status = 1")
    Page<ProductEntity> findBySubCategoryIdAndStatusActive(@Param("subCategoryId") int subCategoryId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.id = :subCategoryId AND p.status = 1 " +
           "AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%')) " +
           "AND (:gender IS NULL OR p.subCategory.subCategoriesName = :gender) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<ProductEntity> findBySubCategoryIdWithFilters(
            @Param("subCategoryId") int subCategoryId,
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.category.id = :categoryId AND p.status = 1")
    List<ProductEntity> findByCategoryIdAndStatusActive(@Param("categoryId") int categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.category.id = :categoryId AND p.status = 1")
    Page<ProductEntity> findByCategoryIdAndStatusActive(@Param("categoryId") int categoryId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.category.id = :categoryId AND p.status = 1 " +
           "AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%')) " +
           "AND (:gender IS NULL OR p.subCategory.subCategoriesName = :gender) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<ProductEntity> findByCategoryIdWithFilters(
            @Param("categoryId") int categoryId,
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.id IN :subCategoryIds AND p.status = 1")
    List<ProductEntity> findBySubCategoryIdsAndStatusActive(@Param("subCategoryIds") List<Integer> subCategoryIds);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.id IN :subCategoryIds AND p.status = 1")
    Page<ProductEntity> findBySubCategoryIdsAndStatusActive(@Param("subCategoryIds") List<Integer> subCategoryIds, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.subCategory.id IN :subCategoryIds AND p.status = 1 " +
           "AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%')) " +
           "AND (:gender IS NULL OR p.subCategory.subCategoriesName = :gender) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<ProductEntity> findBySubCategoryIdsWithFilters(
            @Param("subCategoryIds") List<Integer> subCategoryIds,
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "JOIN p.discountDetails dd " +
           "JOIN dd.discount d " +
           "WHERE p.status = 1 AND dd.status = 1 AND d.status = 1 " +
           "AND d.startDate <= CURRENT_DATE AND d.endDate >= CURRENT_DATE " +
           "AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%')) " +
           "AND (:gender IS NULL OR p.subCategory.subCategoriesName = :gender) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<ProductEntity> findDiscountedProductsWithFilters(
            @Param("search") String search,
            @Param("gender") String gender,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            Pageable pageable);
}
