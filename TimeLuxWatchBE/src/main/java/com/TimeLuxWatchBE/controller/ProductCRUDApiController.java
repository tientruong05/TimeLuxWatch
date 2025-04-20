package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.service.CategoryService;
import com.TimeLuxWatchBE.service.ProductService;
import com.TimeLuxWatchBE.service.SubCategoryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/crud/products")
public class ProductCRUDApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer subCategoryId,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);

        Integer statusInt = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusInt = Integer.parseInt(status);
            } catch (NumberFormatException e) {
                statusInt = null;
            }
        }

        Page<ProductDTO> productPage = productService.getFilteredProducts(search, categoryId, subCategoryId, statusInt, pageable);

        response.put("products", productPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalItems", productPage.getTotalElements());
        response.put("pageSize", size);
        response.put("search", search);
        response.put("categoryId", categoryId);
        response.put("subCategoryId", subCategoryId);
        response.put("status", status);
        response.put("categories", categoryService.getAllCategories());
        response.put("subcategories", subCategoryService.getAllSubCategories());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveProduct(
            @RequestParam("name") String name,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("subCategoryId") Integer subCategoryId,
            @RequestParam("price") String priceStr,
            @RequestParam("qty") Integer qty,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "id", defaultValue = "0") Integer id,
            @RequestParam(value = "existingImage", required = false) String existingImage) {
        Map<String, Object> response = new HashMap<>();
        String error = productService.saveProductFromForm(name, categoryId, subCategoryId, priceStr, qty, description, status, imageFile, id, existingImage);
        if (error != null) {
            response.put("error", error);
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "Lưu sản phẩm thành công");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        String error = productService.deleteProductById(id);
        if (error != null) {
            response.put("error", error);
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "Xóa sản phẩm thành công");
        return ResponseEntity.ok(response);
    }
} 