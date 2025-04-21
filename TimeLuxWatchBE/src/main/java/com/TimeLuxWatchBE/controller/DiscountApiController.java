package com.TimeLuxWatchBE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.entity.DiscountDetailEntity;
import com.TimeLuxWatchBE.entity.DiscountEntity;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.repository.CategoryRepository;
import com.TimeLuxWatchBE.repository.DiscountDetailRepository;
import com.TimeLuxWatchBE.repository.DiscountRepository;
import com.TimeLuxWatchBE.repository.ProductRepository;
import com.TimeLuxWatchBE.repository.SubCategoryRepository;
import com.TimeLuxWatchBE.service.DiscountService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discounts")
public class DiscountApiController {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private DiscountDetailRepository discountDetailRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public ResponseEntity<List<DiscountEntity>> getAllDiscounts() {
        List<DiscountEntity> discounts = discountRepository.findAll();
        return ResponseEntity.ok(discounts);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createDiscount(
            @RequestBody DiscountEntity discount,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @RequestParam(value = "subCategoryIds", required = false) List<Integer> subCategoryIds,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds) {
        Map<String, Object> response = new HashMap<>();
        discountRepository.save(discount);
        saveDiscountDetails(discount.getId(), categoryIds, subCategoryIds, productIds);
        response.put("message", "Tạo mã giảm giá thành công");
        response.put("discount", discount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit-data/{id}")
    public ResponseEntity<Map<String, Object>> getDiscountEditData(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        List<SubCategoryEntity> allSubCategories = subCategoryRepository.findAll();
        List<ProductEntity> allProductEntities = productRepository.findAll();
        List<Map<String, Object>> allProducts = allProductEntities.stream()
                .map(p -> Map.<String, Object>of("id", p.getId(), "name", p.getName(), "subCategory", p.getSubCategory()))
                .collect(Collectors.toList());

        response.put("allCategories", allCategories);
        response.put("allSubCategories", allSubCategories);
        response.put("allProducts", allProducts);

        if (id > 0) {
            DiscountEntity discount = discountRepository.findById(id).orElse(null);
            if (discount != null) {
                List<DiscountDetailEntity> details = discountDetailRepository.findByDiscount(discount);
                List<CategoryEntity> selectedCategories = details.stream().filter(d -> d.getCategory() != null).map(DiscountDetailEntity::getCategory).distinct().collect(Collectors.toList());
                List<SubCategoryEntity> selectedSubCategories = details.stream().filter(d -> d.getSubCategory() != null).map(DiscountDetailEntity::getSubCategory).distinct().collect(Collectors.toList());
                List<Integer> selectedProductIds = details.stream().filter(d -> d.getProduct() != null).map(d -> d.getProduct().getId()).distinct().collect(Collectors.toList());
                List<Map<String, Object>> selectedProducts = allProducts.stream()
                    .filter(p -> selectedProductIds.contains((Integer)p.get("id")))
                    .collect(Collectors.toList());

                response.put("discount", discount);
                response.put("selectedCategories", selectedCategories);
                response.put("selectedSubCategories", selectedSubCategories);
                response.put("selectedProducts", selectedProducts);
                
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateDiscount(
            @PathVariable int id,
            @RequestBody DiscountEntity discount,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            @RequestParam(value = "subCategoryIds", required = false) List<Integer> subCategoryIds,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds) {
        Map<String, Object> response = new HashMap<>();
        discount.setId(id);
        
        DiscountEntity existingDiscount = discountRepository.findById(id).orElse(null);
        if (existingDiscount == null) {
            response.put("error", "Không tìm thấy mã giảm giá với ID: " + id);
            return ResponseEntity.notFound().build();
        }
        
        existingDiscount.setDiscountName(discount.getDiscountName());
        existingDiscount.setDiscountValue(discount.getDiscountValue());
        existingDiscount.setStartDate(discount.getStartDate());
        existingDiscount.setEndDate(discount.getEndDate());
        existingDiscount.setStatus(discount.getStatus());
        
        discountRepository.save(existingDiscount);
        
        discountDetailRepository.deleteByDiscountId(id);
        saveDiscountDetails(id, categoryIds, subCategoryIds, productIds);
        
        response.put("message", "Cập nhật mã giảm giá thành công");
        response.put("discount", existingDiscount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteDiscount(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        discountDetailRepository.deleteByDiscountId(id);
        discountService.delete(id);
        response.put("message", "Xóa mã giảm giá thành công");
        return ResponseEntity.ok(response);
    }

    private void saveDiscountDetails(int discountId, List<Integer> categoryIds, List<Integer> subCategoryIds, List<Integer> productIds) {
        DiscountEntity savedDiscount = discountRepository.findById(discountId).orElse(null);
        if (categoryIds != null) {
            for (Integer categoryId : categoryIds) {
                DiscountDetailEntity detail = new DiscountDetailEntity();
                detail.setDiscount(savedDiscount);
                detail.setCategory(categoryRepository.findById(categoryId).orElse(null));
                detail.setStatus(1);
                discountDetailRepository.save(detail);
            }
        }
        if (subCategoryIds != null) {
            for (Integer subCategoryId : subCategoryIds) {
                DiscountDetailEntity detail = new DiscountDetailEntity();
                detail.setDiscount(savedDiscount);
                detail.setSubCategory(subCategoryRepository.findById(subCategoryId).orElse(null));
                detail.setStatus(1);
                discountDetailRepository.save(detail);
            }
        }
        if (productIds != null) {
            for (Integer productId : productIds) {
                DiscountDetailEntity detail = new DiscountDetailEntity();
                detail.setDiscount(savedDiscount);
                detail.setProduct(productRepository.findById(productId).orElse(null));
                detail.setStatus(1);
                discountDetailRepository.save(detail);
            }
        }
    }
} 