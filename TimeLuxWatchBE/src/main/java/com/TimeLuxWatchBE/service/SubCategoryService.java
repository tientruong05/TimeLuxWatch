package com.TimeLuxWatchBE.service;

import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.entity.CategoryEntity;
import java.util.List;

import org.springframework.data.domain.Page;
import com.TimeLuxWatchBE.service.dto.AddBothGendersResult;

public interface SubCategoryService {
    List<SubCategoryEntity> getAllSubCategories();
    
    SubCategoryEntity getSubCategoryById(int id);
    SubCategoryEntity addSubCategory(SubCategoryEntity subCategory);
    SubCategoryEntity updateSubCategory(SubCategoryEntity subCategory);
    void deleteSubCategory(int id);

	Page<SubCategoryEntity> getSubCategoriesByCategory(String categoryName, int page, int size);
    
    List<SubCategoryEntity> findBySubCategoriesName(String subCategoriesName);

    AddBothGendersResult addBothGenders(CategoryEntity category, int status);
}
