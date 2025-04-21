package com.TimeLuxWatchBE.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.dto.ProductDTO;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<ProductEntity> getProductsBySubCategory(int subCategoryId);
    Page<ProductDTO> getProductsBySubCategory(int subCategoryId, String search, String gender, String priceRange, Pageable pageable);
    List<ProductEntity> getProductsByCategory(int categoryId);
    Page<ProductDTO> getProductsByCategory(int categoryId, String search, String gender, String priceRange, Pageable pageable);
    List<ProductEntity> getAllProducts();
    Page<ProductDTO> getAllProducts(String search, String gender, String priceRange, Pageable pageable);
    String getCategoryName(int categoryId);
    String getSubCategoryName(int subCategoryId);
    List<CategoryEntity> getCategoriesByStatus(int status);
    List<CategoryEntity> searchCategoriesByName(String name);
    CategoryEntity getCategoryById(int id);
    List<CategoryEntity> getAllCategories();
    void createCategory(CategoryEntity category);
    void updateCategory(CategoryEntity category);
    void deleteCategory(int id);
    List<ProductEntity> getProductsByBrandName(String brandName);
    Page<ProductDTO> getProductsByBrandName(String brandName, String search, String gender, String priceRange, Pageable pageable);
	ProductEntity getProductById1(Integer productId);
	ProductEntity getProductById(Integer productId);
    Page<ProductDTO> getDiscountedProducts(String search, String gender, String priceRange, Pageable pageable);
    CategoryEntity findOrCreateCategory(String name);
}
