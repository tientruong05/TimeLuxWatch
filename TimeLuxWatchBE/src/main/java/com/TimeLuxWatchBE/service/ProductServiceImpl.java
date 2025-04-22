package com.TimeLuxWatchBE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.TimeLuxWatchBE.dto.CategoryRevenueDTO;
import com.TimeLuxWatchBE.dto.ProductDTO;
import com.TimeLuxWatchBE.dto.HomePageProductDTO;
import com.TimeLuxWatchBE.dto.ProductDetailDTO;
import com.TimeLuxWatchBE.dto.ProductDetailResponseDTO;

import com.TimeLuxWatchBE.entity.CategoryEntity;
import com.TimeLuxWatchBE.entity.DiscountDetailEntity;
import com.TimeLuxWatchBE.entity.DiscountEntity;
import com.TimeLuxWatchBE.entity.ProductEntity;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.repository.DiscountDetailRepository;
import com.TimeLuxWatchBE.repository.ProductRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountDetailRepository discountDetailRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;

    // Constructor mặc định
    public ProductServiceImpl() {
        // Không cần khởi tạo gì thêm vì đã có @Autowired cho các dependency
    }

    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<ProductEntity> getProductById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {
        if (product.getId() > 0) {
            Optional<ProductEntity> existingProduct = productRepository.findById(product.getId());
            if (existingProduct.isPresent() && product.getImage() == null) {
                product.setImage(existingProduct.get().getImage());
            }
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductEntity> getProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    public void addProduct(ProductEntity product) {
        productRepository.save(product);
    }

    @Override
    public void updateProduct(ProductEntity product) {
        productRepository.save(product);
    }

    @Override
    public void removeProduct(int id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductEntity> bestProducts() {
        return productRepository.bestProducts(PageRequest.of(0, 4));
    }

    @Override
    public List<HomePageProductDTO> getBestProductsDTO() {
        List<ProductEntity> entities = bestProducts();
        return entities.stream()
                       .map(HomePageProductDTO::new)
                       .collect(Collectors.toList());
    }

    @Override
    public List<ProductEntity> newProducts() {
        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findAll(pageable).getContent();
    }

    @Override
    public List<HomePageProductDTO> getNewProductsDTO() {
        List<ProductEntity> entities = newProducts();
        return entities.stream()
                       .map(HomePageProductDTO::new)
                       .collect(Collectors.toList());
    }

    @Override
    public List<ProductEntity> getSaleProducts() {
        return productRepository.saleProducts(PageRequest.of(0, 4));
    }

    @Override
    public List<HomePageProductDTO> getSaleProductsDTO() {
        List<ProductEntity> entities = getSaleProducts();
        return entities.stream()
                       .map(HomePageProductDTO::new)
                       .collect(Collectors.toList());
    }

    @Override
    public Page<CategoryRevenueDTO> getCategoryRevenue(Pageable pageable) {
        return productRepository.getCategoryRevenue(pageable);
    }

    @Override
    public Page<ProductEntity> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<ProductEntity> searchProducts(String search, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrSubCategory_Category_NameContainingIgnoreCaseOrSubCategory_SubCategoriesNameContainingIgnoreCase(search, search, search, pageable);
    }

    @Override
    public Page<ProductEntity> findSearchAll(String name, Pageable pageable) {
        return productRepository.findSearchAll(name, pageable);
    }

    @Override
    public Page<ProductDTO> getFilteredProducts(String search, Integer categoryId, String gender, Integer status, Pageable pageable) {
        // Call the updated repository method, passing gender directly
        Page<ProductEntity> productPage = productRepository.findByFilters(search, categoryId, gender, status, pageable);

        return productPage.map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setImage(product.getImage());
            productDTO.setPrice(product.getPrice());
            productDTO.setQty(product.getQty());
            productDTO.setDescription(product.getDescription());
            productDTO.setStatus(product.getStatus());
            productDTO.setSubCategory(product.getSubCategory());
            // Ensure correct IDs are passed for discount calculation
             Integer actualCategoryId = (product.getSubCategory() != null && product.getSubCategory().getCategory() != null)
                                     ? product.getSubCategory().getCategory().getId() : null;
             Integer actualSubCategoryId = (product.getSubCategory() != null) ? product.getSubCategory().getId() : null;
             applyDiscountToProduct(productDTO, product.getId(), actualCategoryId, actualSubCategoryId);
            return productDTO;
        });
    }

    private void applyDiscountToProduct(ProductDTO productDTO, int productId, Integer categoryId, Integer subCategoryId) {
        List<DiscountDetailEntity> discountDetails = discountDetailRepository.findByProductIdAndStatus(productId, 1);

        if (discountDetails.isEmpty() && categoryId != null) {
            discountDetails = discountDetailRepository.findByCategoryIdAndStatus(categoryId, 1);
        }

        if (discountDetails.isEmpty() && subCategoryId != null) {
            discountDetails = discountDetailRepository.findBySubCategoryIdAndStatus(subCategoryId, 1);
        }

        for (DiscountDetailEntity detail : discountDetails) {
            DiscountEntity discount = detail.getDiscount();
            if (discount != null && discount.isActive()) {
                float discountValue = discount.getDiscountValue();
                productDTO.setDiscountPercentage(discountValue);
                float originalPrice = productDTO.getPrice();
                float discountedPrice = originalPrice - (originalPrice * discountValue / 100);
                productDTO.setDiscountedPrice(discountedPrice);
                break;
            }
        }
    }

    @Autowired
    private FlashSaleService flashSaleService;

    @Override
    public boolean isFlashSaleActive() {
        return flashSaleService.isFlashSaleActive();
    }

    @Override
    public LocalDateTime getFlashSaleEndTime() {
        return flashSaleService.getFlashSaleEndTime();
    }

    @Override
    public String saveProductFromForm(String name, Integer categoryId, Integer subCategoryId, String priceStr, Integer qty,
                                      String description, String status, MultipartFile imageFile, Integer id, String existingImage) {
        try {
            ProductEntity product;
            if (id == null || id == 0) {
                product = new ProductEntity();
            } else {
                product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
            }

            product.setId(id != null ? id : 0);
            product.setName(name);
            product.setQty(qty);
            product.setDescription(description);

            // Xử lý giá
            float price;
            try {
                price = Float.parseFloat(priceStr);
                if (price < 0) {
                    return "Giá phải lớn hơn hoặc bằng 0";
                }
            } catch (NumberFormatException e) {
                return "Giá không hợp lệ";
            }
            product.setPrice(price);

            // Xử lý Category
            Optional<CategoryEntity> category = Optional.ofNullable(categoryService.getCategoryById(categoryId));
            if (!category.isPresent()) {
                return "Loại hàng không hợp lệ";
            }

            // Xử lý SubCategory
            Optional<SubCategoryEntity> subCategory = Optional.ofNullable(subCategoryService.getSubCategoryById(subCategoryId));
            if (!subCategory.isPresent()) {
                return "Hãng không hợp lệ";
            }
            if (subCategory.get().getCategory().getId() != categoryId) {
                return "Hãng không thuộc loại hàng đã chọn";
            }
            product.setSubCategory(subCategory.get());

            // Xử lý status
            product.setStatus(status != null && status.equals("1") ? 1 : 0);

            // Handle the single file case for backward compatibility
            try {
                if (imageFile != null && !imageFile.isEmpty()) {
                    // Process the single file case
                    String originalFileName = imageFile.getOriginalFilename();
                    if (originalFileName != null && originalFileName.contains(".")) {
                        // Get the path to the resources/static/photos directory
                        Path resourceDirectory = Paths.get("src","main","resources","static", "photos");
                        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
                        
                        // Add timestamp to ensure unique filenames
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String uniqueFileName = timeStamp + "_" + originalFileName;
                        Path targetPath = Paths.get(absolutePath, uniqueFileName);

                        System.out.println("Target image save path: " + targetPath.toString());

                        Files.createDirectories(targetPath.getParent()); // Ensure directory exists
                        Files.copy(imageFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                        
                        product.setImage(uniqueFileName);
                    }
                }

                // Handle case when no new images are provided
                if (product.getImage() == null) {
                    if (id == null || id == 0) {
                        product.setImage("default.png");
                    } else {
                        if (existingImage == null || existingImage.isEmpty()) {
                            return "Ảnh hiện tại không hợp lệ";
                        }
                        product.setImage(existingImage);
                    }
                }
            } catch (RuntimeException e) {
                return e.getMessage();
            }

            // Lưu sản phẩm
            productRepository.save(product);
            return null; // Thành công, không có lỗi
        } catch (Exception e) {
            return "Lỗi khi lưu sản phẩm: " + e.getMessage();
        }
    }
    
    /**
     * Enhanced version of saveProductFromForm that handles multiple image files
     */
    public String saveProductWithMultipleImages(String name, Integer categoryId, Integer subCategoryId, String priceStr, Integer qty,
                                       String description, String status, List<MultipartFile> imageFiles, Integer id, String existingImage) {
        try {
            ProductEntity product;
            if (id == null || id == 0) {
                product = new ProductEntity();
            } else {
                product = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
            }

            product.setId(id != null ? id : 0);
            product.setName(name);
            product.setQty(qty);
            product.setDescription(description);

            // Xử lý giá
            float price;
            try {
                price = Float.parseFloat(priceStr);
                if (price < 0) {
                    return "Giá phải lớn hơn hoặc bằng 0";
                }
            } catch (NumberFormatException e) {
                return "Giá không hợp lệ";
            }
            product.setPrice(price);

            // Xử lý Category
            Optional<CategoryEntity> category = Optional.ofNullable(categoryService.getCategoryById(categoryId));
            if (!category.isPresent()) {
                return "Loại hàng không hợp lệ";
            }

            // Xử lý SubCategory
            Optional<SubCategoryEntity> subCategory = Optional.ofNullable(subCategoryService.getSubCategoryById(subCategoryId));
            if (!subCategory.isPresent()) {
                return "Hãng không hợp lệ";
            }
            if (subCategory.get().getCategory().getId() != categoryId) {
                return "Hãng không thuộc loại hàng đã chọn";
            }
            product.setSubCategory(subCategory.get());

            // Xử lý status
            product.setStatus(status != null && status.equals("1") ? 1 : 0);
            
            // Xử lý upload nhiều ảnh
            try {
                if (imageFiles != null && !imageFiles.isEmpty()) {
                    StringBuilder imageNames = new StringBuilder();
                    
                    for (MultipartFile file : imageFiles) {
                        if (file != null && !file.isEmpty()) {
                            String originalFileName = file.getOriginalFilename();
                            if (originalFileName == null || !originalFileName.contains(".")) {
                                continue; // Skip invalid files
                            }

                            try {
                                // Get the path to the resources/static/photos directory
                                Path resourceDirectory = Paths.get("src","main","resources","static", "photos");
                                String absolutePath = resourceDirectory.toFile().getAbsolutePath();
                                
                                // Add timestamp to ensure unique filenames
                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                String uniqueFileName = timeStamp + "_" + originalFileName;
                                Path targetPath = Paths.get(absolutePath, uniqueFileName);

                                System.out.println("Target image save path: " + targetPath.toString());

                                Files.createDirectories(targetPath.getParent()); // Ensure directory exists
                                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                                
                                // Add semicolon as delimiter between image names
                                if (imageNames.length() > 0) {
                                    imageNames.append(";");
                                }
                                imageNames.append(uniqueFileName);
                            } catch (IOException e) {
                                System.err.println("Error saving file: " + originalFileName + " - " + e.getMessage());
                                // Continue with next file even if one fails
                            }
                        }
                    }
                    
                    // Only update the image if we successfully saved at least one
                    if (imageNames.length() > 0) {
                        product.setImage(imageNames.toString());
                    }
                }

                // Handle case when no new images are provided
                if (product.getImage() == null) {
                    if (id == null || id == 0) {
                        product.setImage("default.png");
                    } else {
                        if (existingImage == null || existingImage.isEmpty()) {
                            return "Ảnh hiện tại không hợp lệ";
                        }
                        product.setImage(existingImage);
                    }
                }
            } catch (RuntimeException e) {
                return e.getMessage();
            }

            // Lưu sản phẩm
            productRepository.save(product);
            return null; // Thành công, không có lỗi
        } catch (Exception e) {
            return "Lỗi khi lưu sản phẩm: " + e.getMessage();
        }
    }

    @Override
    public String deleteProductById(int id) {
        try {
            productRepository.deleteById(id);
            return null; // Thành công, không có lỗi
        } catch (Exception e) {
            return "Lỗi khi xóa sản phẩm: " + e.getMessage();
        }
    }

    @Override
    public ProductDetailResponseDTO getProductDetailAndRelated(int id) {
        Optional<ProductEntity> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return null; // Or throw an exception
        }

        ProductEntity product = productOptional.get();
        ProductDetailDTO productDetailDTO = new ProductDetailDTO(product);

        // Fetch related products (same subcategory, excluding self)
        List<ProductEntity> relatedEntities = productRepository.findBySubCategoryId(product.getSubCategory().getId())
            .stream()
            .filter(p -> p.getId() != product.getId()) // Exclude the product itself
            .limit(6) // Limit results
            .collect(Collectors.toList());

        // Map related entities to HomePageProductDTO
        List<HomePageProductDTO> relatedProductDTOs = relatedEntities.stream()
            .map(HomePageProductDTO::new)
            .collect(Collectors.toList());

        return new ProductDetailResponseDTO(productDetailDTO, relatedProductDTOs);
    }
}
