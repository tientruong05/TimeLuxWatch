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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDiscountById(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        DiscountEntity discount = discountRepository.findById(id).orElse(null);
        if (discount != null) {
            List<CategoryEntity> categories = categoryRepository.findAll();
            List<SubCategoryEntity> subCategories = subCategoryRepository.findAll();
            List<ProductEntity> products = productRepository.findAll();
            List<DiscountDetailEntity> discountDetails = discountDetailRepository.findByDiscount(discount);

            List<ProductDTO> productDTOs = new ArrayList<>();
            for (ProductEntity product : products) {
                ProductDTO dto = new ProductDTO();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setImage(product.getImage());
                dto.setPrice(product.getPrice());
                dto.setDiscountedPrice(product.getDiscountedPrice());
                dto.setDiscountPercentage(product.getDiscountPercentage());
                dto.setDiscounted(product.isDiscounted());
                productDTOs.add(dto);
            }

            response.put("discount", discount);
            response.put("categories", categories);
            response.put("subCategories", subCategories);
            response.put("products", productDTOs);
            response.put("discountDetails", discountDetails);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Không tìm thấy mã giảm giá với ID: " + id);
            return ResponseEntity.notFound().build();
        }
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
        discountRepository.save(discount);
        discountDetailRepository.deleteByDiscountId(id);
        saveDiscountDetails(id, categoryIds, subCategoryIds, productIds);
        response.put("message", "Cập nhật mã giảm giá thành công");
        response.put("discount", discount);
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