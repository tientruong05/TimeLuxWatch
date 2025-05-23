package com.TimeLuxWatchBE.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TimeLuxWatchBE.entity.SubCategoryEntity;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Integer> {
	@Query("SELECT s FROM SubCategoryEntity s WHERE s.category.name = :categoryName")
    Page<SubCategoryEntity> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);
	
	/**
     * Tìm danh sách danh mục phụ theo ID danh mục chính
     * @param categoryId ID của danh mục chính
     * @return Danh sách các danh mục phụ
     */
    List<SubCategoryEntity> findByCategory_Id(int categoryId);
    
    /**
     * Tìm danh sách danh mục phụ theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách các danh mục phụ
     */
    @Query("SELECT s FROM SubCategoryEntity s WHERE s.status = :status")
    List<SubCategoryEntity> findByStatus(@Param("status") int status);
    
    /**
     * Tìm danh sách danh mục phụ theo tên 
     * @param subCategoriesName Tên danh mục phụ
     * @return Danh sách các danh mục phụ
     */
    List<SubCategoryEntity> findBySubCategoriesName(String subCategoriesName);

    // Add this method to count remaining subcategories for a given category ID
    long countByCategoryId(int categoryId);

    // Check if a subcategory name already exists for a specific category ID (case-insensitive)
    boolean existsByCategory_IdAndSubCategoriesNameIgnoreCase(int categoryId, String subCategoriesName);

    // Check for update: exists for the category AND the ID is different from the one being updated
    boolean existsByCategory_IdAndSubCategoriesNameIgnoreCaseAndIdNot(int categoryId, String subCategoriesName, int currentId);
}
