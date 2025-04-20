package com.TimeLuxWatchBE.service;

import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import java.util.List;

import org.springframework.data.domain.Page;

public interface SubCategoryService {
    List<SubCategoryEntity> getAllSubCategories();
    
    SubCategoryEntity getSubCategoryById(int id);
    void addSubCategory(SubCategoryEntity subCategory);
    void updateSubCategory(SubCategoryEntity subCategory);
    void deleteSubCategory(int id);

	Page<SubCategoryEntity> getSubCategoriesByCategory(String categoryName, int page, int size);
    
}
