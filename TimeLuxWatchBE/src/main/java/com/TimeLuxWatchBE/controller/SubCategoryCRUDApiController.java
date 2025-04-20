package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.service.CategoryService;
import com.TimeLuxWatchBE.service.SubCategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/crud/subcategories")
public class SubCategoryCRUDApiController {

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
            @RequestParam("categoryName") String categoryName,
            @RequestParam("subCategoriesName") String subCategoriesName,
            @RequestParam("status") int status) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra xem danh mục đã tồn tại chưa
            CategoryEntity category = categoryService.getAllCategories().stream()
                    .filter(cat -> cat.getName().equalsIgnoreCase(categoryName.trim()))
                    .findFirst()
                    .orElse(null);

            // Nếu danh mục chưa tồn tại, tạo mới
            if (category == null) {
                category = new CategoryEntity();
                category.setName(categoryName.trim());
                category.setStatus(1);
                categoryService.createCategory(category);
            }

            // Tạo hoặc cập nhật SubCategoryEntity
            SubCategoryEntity subCategory;
            if (id.isPresent() && id.get() > 0) {
                subCategory = subCategoryService.getSubCategoryById(id.get());
                if (subCategory == null) {
                    response.put("error", "Không tìm thấy thương hiệu với ID: " + id.get());
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                subCategory = new SubCategoryEntity();
            }

            subCategory.setSubCategoriesName(subCategoriesName);
            subCategory.setCategory(category);
            subCategory.setStatus(status);

            if (subCategory.getId() == 0) {
                subCategoryService.addSubCategory(subCategory);
                response.put("message", "Thêm thương hiệu thành công");
            } else {
                subCategoryService.updateSubCategory(subCategory);
                response.put("message", "Cập nhật thương hiệu thành công");
            }
            response.put("subCategory", subCategory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi lưu thương hiệu: " + e.getMessage());
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
                response.put("message", "Xóa thương hiệu thành công");
            } else {
                response.put("error", "Không tìm thấy thương hiệu với ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("error", "Lỗi khi xóa thương hiệu: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
} 