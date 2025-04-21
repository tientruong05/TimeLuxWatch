package com.TimeLuxWatchBE.controller;

import com.TimeLuxWatchBE.exception.DuplicateResourceException;
import com.TimeLuxWatchBE.payload.request.SubCategorySaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.service.CategoryService;
import com.TimeLuxWatchBE.service.SubCategoryService;
import com.TimeLuxWatchBE.service.dto.AddBothGendersResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/crud/categories")
public class CategoryCRUDApiController {

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getSubCategoriesByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryName) {
        Map<String, Object> response = new HashMap<>();

        // Xác định danh mục được chọn, nếu không có thì lấy danh mục đầu tiên
        String selectedCategory = Optional.ofNullable(categoryName)
                .filter(name -> !name.trim().isEmpty())
                .orElseGet(() -> categoryService.getAllCategories().stream()
                        .findFirst()
                        .map(CategoryEntity::getName)
                        .orElse(""));

        // Lấy danh sách thương hiệu theo danh mục với phân trang
        Pageable pageable = PageRequest.of(page, size);
        Page<SubCategoryEntity> subCategoryPage = subCategoryService.getSubCategoriesByCategory(selectedCategory, page, size);

        // Truyền dữ liệu vào response
        response.put("subCategories", subCategoryPage.getContent());
        response.put("categories", categoryService.getAllCategories());
        response.put("currentPage", subCategoryPage.getNumber());
        response.put("totalPages", subCategoryPage.getTotalPages());
        response.put("totalItems", subCategoryPage.getTotalElements());
        response.put("pageSize", size);
        response.put("selectedCategory", selectedCategory);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSubCategory(
            @RequestParam(value = "id", required = false) Optional<Integer> id,
            @RequestBody SubCategorySaveRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String categoryName = request.getCategoryName();
            String subCategoriesName = request.getSubCategoriesName();
            int status = request.getStatus();
            boolean isNew = !id.isPresent() || id.get() <= 0;

            // Basic Validation
            if (categoryName == null || categoryName.trim().isEmpty()) {
                response.put("error", "Tên thương hiệu không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            if (subCategoriesName == null || subCategoriesName.trim().isEmpty()) {
                response.put("error", "Tên loại sản phẩm không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
             // Prevent "Cả hai giới tính" in edit mode
            if (!isNew && "Cả hai giới tính".equalsIgnoreCase(subCategoriesName.trim())) {
                 response.put("error", "Không thể cập nhật thành 'Cả hai giới tính'.");
                 return ResponseEntity.badRequest().body(response);
            }

            // Find or create the category
            CategoryEntity category = categoryService.findOrCreateCategory(categoryName.trim());

            // Handle "Cả hai giới tính" specifically for NEW entries
            if (isNew && "Cả hai giới tính".equalsIgnoreCase(subCategoriesName.trim())) {
                AddBothGendersResult addResult = subCategoryService.addBothGenders(category, status);

                if (addResult.getErrorMessage() != null) {
                    response.put("error", addResult.getErrorMessage());
                    return ResponseEntity.badRequest().body(response);
                }

                if (addResult.isFullySuccessful()) {
                    response.put("message", "Đã thêm Đồng hồ nam và Đồng hồ nữ thành công.");
                } else if (addResult.isPartiallySuccessful()) {
                    response.put("message", "Đã thêm thương hiệu mới thành công.");
                } else if (addResult.isNoOp()) {
                    response.put("message", "Cả Đồng hồ nam và Đồng hồ nữ đã tồn tại.");
                } else {
                     response.put("message", "Hoàn thành xử lý 'Cả hai giới tính'."); // Fallback
                }
                // We don't return a single subCategory in this case
                return ResponseEntity.ok(response);

            } else {
                // --- Standard Add/Update for a single subcategory --- 
                SubCategoryEntity subCategory;
                if (isNew) {
                    subCategory = new SubCategoryEntity();
                } else {
                    subCategory = subCategoryService.getSubCategoryById(id.get());
                    if (subCategory == null) {
                        response.put("error", "Không tìm thấy bản ghi với ID: " + id.get());
                        return ResponseEntity.badRequest().body(response);
                    }
                }

                subCategory.setSubCategoriesName(subCategoriesName.trim());
                subCategory.setCategory(category);
                subCategory.setStatus(status);

                SubCategoryEntity savedSubCategory;
                if (isNew) {
                    savedSubCategory = subCategoryService.addSubCategory(subCategory);
                    response.put("message", "Thêm thương hiệu mới thành công");
                } else {
                    savedSubCategory = subCategoryService.updateSubCategory(subCategory);
                    response.put("message", "Cập nhật loại sản phẩm thành công");
                }
                response.put("subCategory", savedSubCategory);
                return ResponseEntity.ok(response);
            }

        } catch (DuplicateResourceException e) {
            System.err.println("Duplicate detected: " + e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            System.err.println("Error saving subcategory: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = "Lỗi khi lưu loại sản phẩm: " + e.getMessage();
            response.put("error", errorMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteSubCategory(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<SubCategoryEntity> subCategoryOpt = Optional.ofNullable(subCategoryService.getSubCategoryById(id));
            if (subCategoryOpt.isPresent()) {
                subCategoryService.deleteSubCategory(id);
                response.put("message", "Xóa loại sản phẩm thành công");
            } else {
                response.put("error", "Không tìm thấy loại sản phẩm với ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting subcategory: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Lỗi khi xóa loại sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
} 