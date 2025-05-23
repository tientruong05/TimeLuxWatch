package com.TimeLuxWatchBE.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.entity.DiscountEntity;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.repository.DiscountDetailRepository;
import com.TimeLuxWatchBE.repository.ProductRepository;
import com.TimeLuxWatchBE.repository.DiscountRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleServiceImpl.class);

    @Autowired
    private DiscountDetailRepository discountDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Override
    public boolean isFlashSaleActive() {
        try {
            boolean isActive = discountRepository.findActiveFlashSale().isPresent();
            if (!isActive) {
                isActive = discountRepository.findActiveFlashSaleNative().isPresent();
            }
            logger.debug("Flash sale active status: {}", isActive);
            return isActive;
        } catch (Exception e) {
            logger.error("Error checking flash sale status", e);
            return false; // Default to false if error occurs
        }
    }

    @Override
    public LocalDateTime getFlashSaleEndTime() {
        LocalDateTime defaultEndTime = LocalDateTime.now().plusHours(1); // Default fallback
        try {
            Optional<DiscountEntity> activeSale = discountRepository.findActiveFlashSale();

            if (activeSale.isPresent()) {
                DiscountEntity discount = activeSale.get();
                logger.info("Found active flash sale (JPQL): ID={}, EndDate={}, SystemZone={}", discount.getId(), discount.getEndDate(), ZoneId.systemDefault());

                // More robust way to convert java.util.Date to LocalDateTime
                LocalDateTime endDateTime;
                try {
                    Date utilDate = discount.getEndDate();
                    endDateTime = LocalDateTime.ofInstant(
                        utilDate.toInstant(), ZoneId.systemDefault())
                        .withHour(23).withMinute(59).withSecond(59);
                } catch (UnsupportedOperationException e) {
                    // Fallback if toInstant() is not supported (for java.sql.Date)
                    java.sql.Date sqlDate = new java.sql.Date(discount.getEndDate().getTime());
                    endDateTime = sqlDate.toLocalDate().atTime(23, 59, 59);
                    logger.info("Used sql.Date conversion fallback");
                }

                logger.info("Converted end date to LocalDateTime: {}", endDateTime);
                return endDateTime;
            } else {
                logger.warn("No active flash sale found via JPQL, trying native query.");
                Optional<DiscountEntity> activeSaleNative = discountRepository.findActiveFlashSaleNative();
                if (activeSaleNative.isPresent()) {
                    DiscountEntity discount = activeSaleNative.get();
                    logger.info("Found active flash sale (Native): ID={}, EndDate={}, SystemZone={}", discount.getId(), discount.getEndDate(), ZoneId.systemDefault());

                    // More robust way to convert java.util.Date to LocalDateTime
                    LocalDateTime endDateTime;
                    try {
                        Date utilDate = discount.getEndDate();
                        endDateTime = LocalDateTime.ofInstant(
                            utilDate.toInstant(), ZoneId.systemDefault())
                            .withHour(23).withMinute(59).withSecond(59);
                    } catch (UnsupportedOperationException e) {
                        // Fallback if toInstant() is not supported (for java.sql.Date)
                        java.sql.Date sqlDate = new java.sql.Date(discount.getEndDate().getTime());
                        endDateTime = sqlDate.toLocalDate().atTime(23, 59, 59);
                        logger.info("Used sql.Date conversion fallback");
                    }

                    logger.info("Converted end date to LocalDateTime: {}", endDateTime);
                    return endDateTime;
                } else {
                    logger.error("No active flash sale found via JPQL or Native Query. Defaulting to 1 hour from now.");
                    return defaultEndTime;
                }
            }
        } catch (Exception e) {
            // Log the exception for detailed debugging
            logger.error("Error occurred while fetching flash sale end time. Defaulting to 1 hour from now.", e);
            return defaultEndTime;
        }
    }

    @Override
    public List<ProductDTO> getFlashSaleProducts() {
        try {
            Optional<DiscountEntity> discountOpt = discountRepository.findActiveFlashSale();
            if (!discountOpt.isPresent()) {
                discountOpt = discountRepository.findActiveFlashSaleNative();
            }
            return getDiscountSaleProducts(discountOpt);
        } catch (Exception e) {
            logger.error("Error getting flash sale products", e);
            return List.of(); // Return empty list on error
        }
    }

    @Override
    public Page<ProductDTO> getFlashSaleProductsPaged(Pageable pageable) {
        try {
            // Lấy thông tin khuyến mãi đang hoạt động
            Optional<DiscountEntity> discountOpt = discountRepository.findActiveFlashSale();
            if (!discountOpt.isPresent()) {
                discountOpt = discountRepository.findActiveFlashSaleNative();
            }

            if (discountOpt.isPresent()) {
                // Lấy tất cả sản phẩm trong flash sale
                List<ProductDTO> allProducts = getDiscountSaleProducts(discountOpt);

                // Tạo Page thủ công từ List và Pageable
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), allProducts.size());

                if (start > allProducts.size()) {
                    return new org.springframework.data.domain.PageImpl<>(
                        java.util.Collections.emptyList(), pageable, allProducts.size());
                }

                List<ProductDTO> pageContent = allProducts.subList(start, end);

                return new org.springframework.data.domain.PageImpl<>(
                    pageContent, pageable, allProducts.size());
            }

            // Trả về Page rỗng nếu không có flash sale
            return new org.springframework.data.domain.PageImpl<>(
                java.util.Collections.emptyList(), pageable, 0);
        } catch (Exception e) {
            logger.error("Error getting paged flash sale products", e);
            // Trả về Page rỗng khi có lỗi
            return new org.springframework.data.domain.PageImpl<>(
                java.util.Collections.emptyList(), pageable, 0);
        }
    }

    @Override
    public List<ProductDTO> getFlashSaleProductsForHomepage() {
        List<ProductDTO> allProducts = getFlashSaleProducts();
        return allProducts.size() > 4 ? allProducts.subList(0, 4) : allProducts;
    }

    private List<ProductDTO> getDiscountSaleProducts(Optional<DiscountEntity> discountOpt) {
        return discountOpt
                .map(discount -> {
                    List<Integer> productIds = discountDetailRepository.findProductIdsByDiscountId(discount.getId());
                    logger.debug("Found {} product IDs for discount ID {}", productIds.size(), discount.getId());

                    // Lấy danh sách category ID và sub category ID từ discount_details
                    List<Integer> categoryIds = discountDetailRepository.findCategoryIdsByDiscountId(discount.getId());
                    List<Integer> subCategoryIds = discountDetailRepository.findSubCategoryIdsByDiscountId(discount.getId());

                    logger.debug("Found {} category IDs and {} sub-category IDs for discount ID {}",
                               categoryIds.size(), subCategoryIds.size(), discount.getId());

                    // Lấy sản phẩm từ product_id trực tiếp
                    List<ProductEntity> directProducts = productRepository.findByIdInAndStatus(productIds, 1);

                    // Lấy sản phẩm thuộc các danh mục
                    List<ProductEntity> categoryProducts = productRepository.findByCategoryIdsAndStatus(categoryIds, 1);

                    // Lấy sản phẩm thuộc các danh mục con
                    List<ProductEntity> subCategoryProducts = productRepository.findBySubCategoryIdsAndStatus(subCategoryIds, 1);

                    // Gộp các danh sách sản phẩm (loại bỏ trùng lặp)
                    List<ProductEntity> allProducts = new java.util.ArrayList<>(directProducts);

                    // Thêm categoryProducts nhưng loại bỏ các sản phẩm đã có trong directProducts
                    for (ProductEntity product : categoryProducts) {
                        if (!containsProduct(allProducts, product.getId())) {
                            allProducts.add(product);
                        }
                    }

                    // Thêm subCategoryProducts nhưng loại bỏ các sản phẩm đã có
                    for (ProductEntity product : subCategoryProducts) {
                        if (!containsProduct(allProducts, product.getId())) {
                            allProducts.add(product);
                        }
                    }

                    logger.debug("Total flash sale products after combining: {}", allProducts.size());

                    return allProducts.stream()
                            .map(product -> mapToProductDTO(product, discount))
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }

    // Helper method to check if a product already exists in the list
    private boolean containsProduct(List<ProductEntity> products, int productId) {
        return products.stream().anyMatch(p -> p.getId() == productId);
    }

    @Override
    public Optional<DiscountEntity> getCurrentFlashSale() {
        try {
            Optional<DiscountEntity> sale = discountRepository.findActiveFlashSale();
            if (!sale.isPresent()) {
                sale = discountRepository.findActiveFlashSaleNative();
            }
            return sale;
        } catch (Exception e) {
            logger.error("Error getting current flash sale info", e);
            return Optional.empty();
        }
    }

    @Override
    public List<DiscountEntity> getAllActiveDiscounts() {
        try {
            return discountRepository.findAllActiveDiscounts();
        } catch (Exception e) {
            logger.error("Error getting all active discounts", e);
            return List.of();
        }
    }

    @Override
    public int calculateSellProgress(int availableQuantity, int totalQuantity) {
        if (totalQuantity <= 0) return 0;
        int soldQuantity = totalQuantity - availableQuantity;
        return Math.min(100, (soldQuantity * 100) / totalQuantity);
    }

    @Override
    public boolean isProductInFlashSale(int productId) {
        return getCurrentFlashSale()
                .map(discount -> {
                    boolean exists = discountDetailRepository.existsByDiscountIdAndProductId(
                        discount.getId(), productId);
                    logger.trace("Product ID {} exists in flash sale ID {}: {}", productId, discount.getId(), exists);
                    return exists;
                 })
                .orElse(false);
    }

    private ProductDTO mapToProductDTO(ProductEntity product, DiscountEntity discount) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        dto.setQty(product.getQty());

        // Calculate selling progress based on stock percentage
        int currentQty = product.getQty();
        int estimatedInitialQty = Math.max(30, currentQty * 2); // Simple estimation
        dto.setSellProgress(calculateSellProgress(currentQty, estimatedInitialQty));

        // Apply discount information
        float discountValue = discount.getDiscountValue();
        float discountedPrice = product.getPrice() * (1 - discountValue / 100);
        dto.setDiscountedPrice(discountedPrice);
        dto.setDiscountPercentage(discountValue);

        logger.trace("Mapped ProductEntity ID {} to ProductDTO with discount {}%", product.getId(), discountValue);
        return dto;
    }
}
