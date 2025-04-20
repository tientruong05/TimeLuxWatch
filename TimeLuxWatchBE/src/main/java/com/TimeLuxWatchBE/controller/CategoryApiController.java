package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.TimeLuxWatchBE.config.FlashSaleManager;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.service.CategoryService;
import com.TimeLuxWatchBE.repository.SubCategoryRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @GetMapping("/list-product")
    public ResponseEntity<Map<String, Object>> showCategory(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer subCategoryId,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) Boolean discount,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);

        // Khởi tạo các biến để xác định loại trang và cách lọc
        Integer effectiveCategoryId = categoryId; // Hãng đồng hồ
        String effectiveGender = gender;          // Giới tính (nam/nữ)
        String pageTitleGender = null;
        boolean isGenderPage = false;
        
        // Xử lý trường hợp đặc biệt: khi gender được chỉ định trực tiếp
        if (gender != null) {
            isGenderPage = true;
            pageTitleGender = gender.equals("male") ? "Đồng hồ nam" : "Đồng hồ nữ";
        }

        // Lấy sản phẩm dựa trên các bộ lọc hiện tại
        Page<ProductDTO> productPage = determineProductPage(effectiveCategoryId, subCategoryId, brandName, discount, search, effectiveGender, priceRange, pageable);

        // Xác định tên danh mục hiển thị
        String categoryName;
        if (pageTitleGender != null) {
            categoryName = pageTitleGender;
        } else if (Boolean.TRUE.equals(discount)) {
            categoryName = "Hàng giảm giá";
        } else if (brandName != null && !brandName.isEmpty()) {
            categoryName = brandName;
        } else if (subCategoryId != null) {
            categoryName = categoryService.getSubCategoryName(subCategoryId);
        } else if (categoryId != null) {
            categoryName = categoryService.getCategoryName(categoryId);
        } else {
            categoryName = "Tất cả sản phẩm";
        }
        
        // Xác định xem có hiện bộ lọc Gender hay không 
        boolean showGenderFilter = !isGenderPage;

        // Đưa dữ liệu vào response
        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalPages", productPage.getTotalPages());
        response.put("pageSize", productPage.getSize());
        response.put("totalItems", productPage.getTotalElements());
        response.put("categoryName", categoryName);
        response.put("categoryId", effectiveCategoryId);
        response.put("isGenderPage", isGenderPage);
        response.put("subCategoryId", subCategoryId);
        response.put("brandName", brandName);
        response.put("discount", discount);
        response.put("search", search);
        response.put("gender", effectiveGender);
        response.put("priceRange", priceRange);
        response.put("showGenderFilter", showGenderFilter);
        response.put("isFlashSaleActive", FlashSaleManager.isFlashSaleActive());
        response.put("flashSaleEndTime", FlashSaleManager.getEndTime());

        return ResponseEntity.ok(response);
    }

    private Page<ProductDTO> determineProductPage(Integer categoryId, Integer subCategoryId, String brandName,
                                                  Boolean discount, String search, String gender, String priceRange,
                                                  Pageable pageable) {
        if (Boolean.TRUE.equals(discount)) {
            return categoryService.getDiscountedProducts(search, gender, priceRange, pageable);
        } else if (brandName != null && !brandName.isEmpty()) {
            return categoryService.getProductsByBrandName(brandName, search, gender, priceRange, pageable);
        } else if (subCategoryId != null) {
            return categoryService.getProductsBySubCategory(subCategoryId, search, gender, priceRange, pageable);
        } else if (categoryId != null) {
            return categoryService.getProductsByCategory(categoryId, search, gender, priceRange, pageable);
        } else {
            return categoryService.getAllProducts(search, gender, priceRange, pageable);
        }
    }
} 