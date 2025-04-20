package com.TimeLuxWatchBE.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.entity.DiscountEntity;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.repository.CategoryRepository;
import com.TimeLuxWatchBE.repository.DiscountDetailRepository;
import com.TimeLuxWatchBE.repository.DiscountRepository;
import com.TimeLuxWatchBE.repository.ProductRepository;
import com.TimeLuxWatchBE.repository.SubCategoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountDetailRepository discountDetailRepository;

    @Override
    public List<CategoryEntity> getCategoriesByStatus(int status) {
        return categoryRepository.findByStatus(status);
    }

    @Override
    public List<CategoryEntity> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    @Override
    public CategoryEntity getCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(CategoryEntity category) {
        if (category != null) {
            categoryRepository.save(category);
        }
    }

    @Override
    public void updateCategory(CategoryEntity category) {
        if (category != null) {
            categoryRepository.save(category);
        }
    }

    @Override
    public void deleteCategory(int id) {
        if (id > 0) {
            categoryRepository.deleteById(id);
        }
    }

    @Override
    public List<ProductEntity> getProductsBySubCategory(int subCategoryId) {
        return productRepository.findBySubCategoryId(subCategoryId);
    }

    @Override
    public Page<ProductDTO> getProductsBySubCategory(int subCategoryId, String search, String gender, String priceRange, Pageable pageable) {
        // --- START: Gender Translation Logic ---
        String dbGender = null;
        if ("male".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nam";
        } else if ("female".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nữ";
        } else {
            dbGender = gender;
        }
        // --- END: Gender Translation Logic ---

        // --- START: Price Range Parsing Logic ---
        Float minPrice = null;
        Float maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            if (priceRange.contains("-")) {
                String[] parts = priceRange.split("-");
                try {
                    minPrice = Float.parseFloat(parts[0]);
                    maxPrice = Float.parseFloat(parts[1]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format: {}", priceRange);
                }
            } else if (priceRange.endsWith("+")) {
                try {
                    minPrice = Float.parseFloat(priceRange.substring(0, priceRange.length() - 1));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format for open range: {}", priceRange);
                }
            }
        }
        // --- END: Price Range Parsing Logic ---

        Page<ProductEntity> productPage = productRepository.findBySubCategoryIdWithFilters(subCategoryId, search, dbGender, minPrice, maxPrice, pageable);
        return mapProductPageToDtoPage(productPage, pageable);
    }

    @Override
    public List<ProductEntity> getProductsByCategory(int categoryId) {
        return productRepository.findByCategoryIdAndStatusActive(categoryId);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(int categoryId, String search, String gender, String priceRange, Pageable pageable) {
        // --- START: Gender Translation Logic ---
        String dbGender = null;
        if ("male".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nam";
        } else if ("female".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nữ";
        } else {
            dbGender = gender;
        }
        // --- END: Gender Translation Logic ---

        // --- START: Price Range Parsing Logic ---
        Float minPrice = null;
        Float maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            if (priceRange.contains("-")) {
                String[] parts = priceRange.split("-");
                try {
                    minPrice = Float.parseFloat(parts[0]);
                    maxPrice = Float.parseFloat(parts[1]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format: {}", priceRange);
                }
            } else if (priceRange.endsWith("+")) {
                try {
                    minPrice = Float.parseFloat(priceRange.substring(0, priceRange.length() - 1));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format for open range: {}", priceRange);
                }
            }
        }
        // --- END: Price Range Parsing Logic ---

        Page<ProductEntity> productPage = productRepository.findByCategoryIdWithFilters(categoryId, search, dbGender, minPrice, maxPrice, pageable);
        return mapProductPageToDtoPage(productPage, pageable);
    }

    @Override
    public String getCategoryName(int categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryEntity::getName)
                .orElse("");
    }

    @Override
    public String getSubCategoryName(int subCategoryId) {
        return subCategoryRepository.findById(subCategoryId)
                .map(SubCategoryEntity::getSubCategoriesName)
                .orElse("");
    }

    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAllActive();
    }

    @Override
    public Page<ProductDTO> getAllProducts(String search, String gender, String priceRange, Pageable pageable) {
        // --- START: Gender Translation Logic ---
        String dbGender = null;
        if ("male".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nam";
        } else if ("female".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nữ";
        } else {
            dbGender = gender;
        }
        // --- END: Gender Translation Logic ---

        // --- START: Price Range Parsing Logic ---
        Float minPrice = null;
        Float maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            if (priceRange.contains("-")) {
                String[] parts = priceRange.split("-");
                try {
                    minPrice = Float.parseFloat(parts[0]);
                    maxPrice = Float.parseFloat(parts[1]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format: {}", priceRange);
                }
            } else if (priceRange.endsWith("+")) {
                try {
                    minPrice = Float.parseFloat(priceRange.substring(0, priceRange.length() - 1));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format for open range: {}", priceRange);
                }
            }
        }
        // --- END: Price Range Parsing Logic ---

        Page<ProductEntity> productPage = productRepository.findAllWithFilters(search, dbGender, minPrice, maxPrice, pageable);
        return mapProductPageToDtoPage(productPage, pageable);
    }

    @Override
    public List<ProductEntity> getProductsByBrandName(String brandName) {
        return subCategoryRepository.findBySubCategoriesName(brandName).stream()
                .map(SubCategoryEntity::getId)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        ids -> productRepository.findBySubCategoryIdsAndStatusActive(ids)));
    }

    @Override
    public Page<ProductDTO> getProductsByBrandName(String brandName, String search, String gender, String priceRange, Pageable pageable) {
        // --- START: Gender Translation Logic ---
        String dbGender = null;
        if ("male".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nam";
        } else if ("female".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nữ";
        } else {
            dbGender = gender;
        }
        // --- END: Gender Translation Logic ---

        // --- START: Price Range Parsing Logic ---
        Float minPrice = null;
        Float maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            if (priceRange.contains("-")) {
                String[] parts = priceRange.split("-");
                try {
                    minPrice = Float.parseFloat(parts[0]);
                    maxPrice = Float.parseFloat(parts[1]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format: {}", priceRange);
                }
            } else if (priceRange.endsWith("+")) {
                try {
                    minPrice = Float.parseFloat(priceRange.substring(0, priceRange.length() - 1));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format for open range: {}", priceRange);
                }
            }
        }
        // --- END: Price Range Parsing Logic ---

        List<Integer> subCategoryIds = subCategoryRepository.findBySubCategoriesName(brandName).stream()
                .map(SubCategoryEntity::getId)
                .collect(Collectors.toList());
        Page<ProductEntity> productPage = productRepository.findBySubCategoryIdsWithFilters(subCategoryIds, search, dbGender, minPrice, maxPrice, pageable);
        return mapProductPageToDtoPage(productPage, pageable);
    }

    @Override
    public Page<ProductDTO> getDiscountedProducts(String search, String gender, String priceRange, Pageable pageable) {
        // --- START: Gender Translation Logic ---
        String dbGender = null;
        if ("male".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nam";
        } else if ("female".equalsIgnoreCase(gender)) {
            dbGender = "Đồng hồ nữ";
        } else {
            dbGender = gender;
        }
        // --- END: Gender Translation Logic ---

        // --- START: Price Range Parsing Logic ---
        Float minPrice = null;
        Float maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            if (priceRange.contains("-")) {
                String[] parts = priceRange.split("-");
                try {
                    minPrice = Float.parseFloat(parts[0]);
                    maxPrice = Float.parseFloat(parts[1]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format: {}", priceRange);
                }
            } else if (priceRange.endsWith("+")) {
                try {
                    minPrice = Float.parseFloat(priceRange.substring(0, priceRange.length() - 1));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price range format for open range: {}", priceRange);
                }
            }
        }
        // --- END: Price Range Parsing Logic ---

        Page<ProductEntity> productPage = productRepository.findDiscountedProductsWithFilters(search, dbGender, minPrice, maxPrice, pageable);
        return mapProductPageToDtoPage(productPage, pageable);
    }

    @Override
    public ProductEntity getProductById1(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public ProductEntity getProductById(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }

    private Page<ProductDTO> mapProductPageToDtoPage(Page<ProductEntity> productPage, Pageable pageable) {
        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    private ProductDTO mapToProductDTO(ProductEntity product) {
        List<DiscountEntity> activeDiscounts = discountRepository.findAllActiveDiscountsForProduct(product.getId(),
                product.getSubCategory().getCategory().getId(), product.getSubCategory().getId());
        
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        dto.setQty(product.getQty());
        dto.setStatus(product.getStatus());
        dto.setDescription(product.getDescription());
        dto.setSubCategory(product.getSubCategory());
        
        Optional<DiscountEntity> bestDiscount = activeDiscounts.stream()
            .max((d1, d2) -> Float.compare(d1.getDiscountValue(), d2.getDiscountValue()));

        if (bestDiscount.isPresent()) {
            DiscountEntity discount = bestDiscount.get();
            float discountValue = discount.getDiscountValue();
            float discountedPrice = product.getPrice() * (1 - discountValue / 100);
            dto.setDiscountedPrice(discountedPrice);
            dto.setDiscountPercentage(discountValue);
            dto.setDiscounted(true);
            logger.trace("Mapped ProductEntity ID {} to ProductDTO with active discount {}%", product.getId(), discountValue);
        } else {
            dto.setDiscountedPrice(null);
            dto.setDiscountPercentage(null);
            dto.setDiscounted(false);
            logger.trace("Mapped ProductEntity ID {} to ProductDTO with no active discount", product.getId());
        }
        
        int currentQty = product.getQty();
        int estimatedInitialQty = Math.max(30, currentQty * 2);
        dto.setSellProgress(calculateSellProgress(currentQty, estimatedInitialQty));

        return dto;
    }
    
    private ProductDTO mapToProductDTOWithActiveDiscounts(ProductEntity product, java.util.Map<Integer, DiscountEntity> activeDiscountMap) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        dto.setQty(product.getQty());
        dto.setStatus(product.getStatus());
        dto.setDescription(product.getDescription());
        dto.setSubCategory(product.getSubCategory());
        
        Optional<DiscountEntity> bestDiscount = findBestApplicableDiscount(product, activeDiscountMap);

        if (bestDiscount.isPresent()) {
            DiscountEntity discount = bestDiscount.get();
            float discountValue = discount.getDiscountValue();
            float discountedPrice = product.getPrice() * (1 - discountValue / 100);
            dto.setDiscountedPrice(discountedPrice);
            dto.setDiscountPercentage(discountValue);
            dto.setDiscounted(true);
            logger.trace("(Optimized) Mapped ProductEntity ID {} to ProductDTO with discount {}%", product.getId(), discountValue);
        } else {
            dto.setDiscountedPrice(null);
            dto.setDiscountPercentage(null);
            dto.setDiscounted(false);
             logger.warn("(Optimized) Product ID {} was expected to have a discount but none was applied.", product.getId());
        }
        
        int currentQty = product.getQty();
        int estimatedInitialQty = Math.max(30, currentQty * 2);
        dto.setSellProgress(calculateSellProgress(currentQty, estimatedInitialQty));

        return dto;
    }
    
    private Optional<DiscountEntity> findBestApplicableDiscount(ProductEntity product, java.util.Map<Integer, DiscountEntity> activeDiscountMap) {
        List<DiscountEntity> applicableDiscounts = new ArrayList<>();

        List<Integer> relatedDiscountIds = discountDetailRepository
                .findDiscountIdsByProductOrCategoryOrSubCategory(
                        product.getId(), 
                        product.getSubCategory().getCategory().getId(), 
                        product.getSubCategory().getId());

        for (Integer discountId : relatedDiscountIds) {
            DiscountEntity discount = activeDiscountMap.get(discountId);
            if (discount != null) {
                 if (discountDetailRepository.existsByDiscountIdAndProductId(discountId, product.getId()) ||
                     discountDetailRepository.existsByDiscountIdAndCategoriesId(discountId, product.getSubCategory().getCategory().getId()) ||
                     discountDetailRepository.existsByDiscountIdAndSubcategoriesId(discountId, product.getSubCategory().getId())) {
                     applicableDiscounts.add(discount);
                 }
            }
        }

        return applicableDiscounts.stream()
                .max((d1, d2) -> Float.compare(d1.getDiscountValue(), d2.getDiscountValue()));
    }

    private boolean containsProduct(List<ProductEntity> products, int productId) {
        return products.stream().anyMatch(p -> p.getId() == productId);
    }

    private int calculateSellProgress(int availableQuantity, int totalQuantity) {
        if (totalQuantity <= 0) return 0;
        int soldQuantity = totalQuantity - availableQuantity;
        return Math.min(100, (int) (((double) soldQuantity / totalQuantity) * 100));
    }

    private boolean filterBySearch(ProductEntity product, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        String lowerSearch = search.toLowerCase();
        return (product.getName() != null && product.getName().toLowerCase().contains(lowerSearch)) ||
               (product.getSubCategory() != null && product.getSubCategory().getSubCategoriesName() != null && product.getSubCategory().getSubCategoriesName().toLowerCase().contains(lowerSearch)) ||
               (product.getSubCategory() != null && product.getSubCategory().getCategory() != null && product.getSubCategory().getCategory().getName() != null && product.getSubCategory().getCategory().getName().toLowerCase().contains(lowerSearch));
    }

    private boolean filterByGender(ProductEntity product, String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return true;
        }
        if (product.getSubCategory() == null || product.getSubCategory().getCategory() == null) {
            return false;
        }
        int categoryId = product.getSubCategory().getCategory().getId();
        if ("male".equalsIgnoreCase(gender)) {
            return categoryId == 1;
        } else if ("female".equalsIgnoreCase(gender)) {
            return categoryId == 2;
        } else {
            return true;
        }
    }

    private boolean filterByPriceRange(ProductEntity product, String priceRange) {
        if (priceRange == null || priceRange.trim().isEmpty()) {
            return true;
        }
        float price = product.getPrice();

        switch (priceRange) {
            case "1-3":
                return price >= 1000000 && price <= 3000000;
            case "3-5":
                return price > 3000000 && price <= 5000000;
            case "5+":
                return price > 5000000;
            default:
                return true;
        }
    }
}
