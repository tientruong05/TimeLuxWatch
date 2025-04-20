package com.TimeLuxWatchBE.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.TimeLuxWatchBE.entity.SubCategoryEntity;
import com.TimeLuxWatchBE.repository.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Override
    public List<SubCategoryEntity> getAllSubCategories() {
        return subCategoryRepository.findAll().stream()
                .filter(subCategory -> subCategory != null)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public SubCategoryEntity getSubCategoryById(int id) {
        return subCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public void addSubCategory(SubCategoryEntity subCategory) {
        Optional.ofNullable(subCategory).ifPresent(subCategoryRepository::save);
    }

    @Override
    public void updateSubCategory(SubCategoryEntity subCategory) {
        Optional.ofNullable(subCategory).ifPresent(subCategoryRepository::save);
    }

    @Override
    public void deleteSubCategory(int id) {
        Optional.of(id).filter(i -> i > 0).ifPresent(subCategoryRepository::deleteById);
    }

    @Override
    public Page<SubCategoryEntity> getSubCategoriesByCategory(String categoryName, int page, int size) {
        return subCategoryRepository.findByCategoryName(categoryName, PageRequest.of(page, size));
    }
}
