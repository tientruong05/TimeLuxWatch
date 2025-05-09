package com.TimeLuxWatchBE.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.TimeLuxWatchBE.dto.CategoryRevenueDTO;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.dto.HomePageProductDTO;
import com.TimeLuxWatchBE.dto.ProductDetailResponseDTO;
import com.TimeLuxWatchBE.entity.ProductEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductEntity> getAllProducts();
    Optional<ProductEntity> getProductById(int id);
    ProductEntity saveProduct(ProductEntity product);
    void deleteProduct(int id);
    List<ProductEntity> getProductsByName(String name);
    void addProduct(ProductEntity product);
    void updateProduct(ProductEntity product);
    void removeProduct(int id);
    List<ProductEntity> bestProducts();
    List<HomePageProductDTO> getBestProductsDTO();
    List<ProductEntity> newProducts();
    List<HomePageProductDTO> getNewProductsDTO();
    List<ProductEntity> getSaleProducts();
    List<HomePageProductDTO> getSaleProductsDTO();
    Page<CategoryRevenueDTO> getCategoryRevenue(Pageable pageable);
    Page<ProductEntity> getAllProducts(Pageable pageable);
    Page<ProductEntity> searchProducts(String search, Pageable pageable);
    Page<ProductEntity> findSearchAll(String name, Pageable pageable);
    Page<ProductDTO> getFilteredProducts(String search, Integer categoryId, String gender, Integer status, Pageable pageable);
    boolean isFlashSaleActive();
    LocalDateTime getFlashSaleEndTime();
    ProductDetailResponseDTO getProductDetailAndRelated(int id);

    String saveProductFromForm(String name, Integer categoryId, Integer subCategoryId, String priceStr, Integer qty,
                               String description, String status, MultipartFile imageFile, Integer id, String existingImage);
    String deleteProductById(int id);
    
    /**
     * Enhanced version of saveProductFromForm that handles multiple image files
     */
    String saveProductWithMultipleImages(String name, Integer categoryId, Integer subCategoryId, String priceStr, Integer qty,
                                        String description, String status, List<MultipartFile> imageFiles, Integer id, String existingImage);
}
