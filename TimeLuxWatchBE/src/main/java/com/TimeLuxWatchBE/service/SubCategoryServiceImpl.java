package com.TimeLuxWatchBE.service;

import com.TimeLuxWatchBE.exception.DuplicateResourceException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.repository.SubCategoryRepository;
import com.TimeLuxWatchBE.repository.CategoryRepository;
import com.TimeLuxWatchBE.service.dto.AddBothGendersResult;
import com.TimeLuxWatchBE.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<SubCategoryEntity> getAllSubCategories() {
        return subCategoryRepository.findAll().stream()
                .filter(subCategory -> subCategory != null)
                .collect(Collectors.toList());
    }

    @Override
    public SubCategoryEntity getSubCategoryById(int id) {
        Optional<SubCategoryEntity> optionalSubCategory = subCategoryRepository.findById(id);
        return optionalSubCategory.orElse(null);
    }

    @Override
    public SubCategoryEntity addSubCategory(SubCategoryEntity subCategory) {
        if (subCategoryRepository.existsByCategory_IdAndSubCategoriesNameIgnoreCase(
                subCategory.getCategory().getId(),
                subCategory.getSubCategoriesName())) {
            throw new DuplicateResourceException(
                "Loại sản phẩm '" + subCategory.getSubCategoriesName() +
                "' đã tồn tại cho thương hiệu này.");
        }
        return subCategoryRepository.save(subCategory);
    }

    @Override
    public SubCategoryEntity updateSubCategory(SubCategoryEntity subCategory) {
        if (!subCategoryRepository.existsById(subCategory.getId())) {
            return null;
        }
        if (subCategoryRepository.existsByCategory_IdAndSubCategoriesNameIgnoreCaseAndIdNot(
                subCategory.getCategory().getId(),
                subCategory.getSubCategoriesName(),
                subCategory.getId())) {
             throw new DuplicateResourceException(
                "Loại sản phẩm '" + subCategory.getSubCategoriesName() +
                "' đã tồn tại cho thương hiệu này.");
        }
        return subCategoryRepository.save(subCategory);
    }

    @Override
    public void deleteSubCategory(int id) {
        Optional<SubCategoryEntity> subCategoryOpt = subCategoryRepository.findById(id);
        if (subCategoryOpt.isPresent()) {
            SubCategoryEntity subCategoryToDelete = subCategoryOpt.get();
            int categoryId = subCategoryToDelete.getCategory().getId();

            subCategoryRepository.deleteById(id);

            long remainingCount = subCategoryRepository.countByCategoryId(categoryId);

            if (remainingCount == 0) {
                categoryRepository.deleteById(categoryId);
                System.out.println("Deleted empty category with ID: " + categoryId);
            }
        } else {
            System.out.println("SubCategory with ID " + id + " not found for deletion.");
        }
    }

    @Override
    public Page<SubCategoryEntity> getSubCategoriesByCategory(String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return subCategoryRepository.findByCategoryName(categoryName, pageable);
    }

    @Override
    public List<SubCategoryEntity> findBySubCategoriesName(String subCategoriesName) {
        return subCategoryRepository.findBySubCategoriesName(subCategoriesName);
    }

    @Override
    public AddBothGendersResult addBothGenders(CategoryEntity category, int status) {
        AddBothGendersResult result = new AddBothGendersResult();
        String maleType = "Đồng hồ nam";
        String femaleType = "Đồng hồ nữ";
        int categoryId = category.getId();

        try {
            // Check for male type
            if (!subCategoryRepository.existsByCategory_IdAndSubCategoriesNameIgnoreCase(categoryId, maleType)) {
                SubCategoryEntity maleSubCategory = new SubCategoryEntity();
                maleSubCategory.setCategory(category);
                maleSubCategory.setSubCategoriesName(maleType);
                maleSubCategory.setStatus(status);
                subCategoryRepository.save(maleSubCategory);
                result.setNamAdded(true);
            } else {
                result.setNamExisted(true);
            }

            // Check for female type
            if (!subCategoryRepository.existsByCategory_IdAndSubCategoriesNameIgnoreCase(categoryId, femaleType)) {
                SubCategoryEntity femaleSubCategory = new SubCategoryEntity();
                femaleSubCategory.setCategory(category);
                femaleSubCategory.setSubCategoriesName(femaleType);
                femaleSubCategory.setStatus(status);
                subCategoryRepository.save(femaleSubCategory);
                result.setNuAdded(true);
            } else {
                result.setNuExisted(true);
            }
        } catch (Exception e) {
            // Log the error (consider using a proper logger)
            System.err.println("Error during addBothGenders for category ID " + categoryId + ": " + e.getMessage());
            e.printStackTrace();
            result.setErrorMessage("Lỗi không mong muốn khi thêm loại sản phẩm: " + e.getMessage());
            // Note: Since it's transactional, partial saves *should* be rolled back,
            // but the result object might reflect attempts made before the error.
        }

        return result;
    }
}
