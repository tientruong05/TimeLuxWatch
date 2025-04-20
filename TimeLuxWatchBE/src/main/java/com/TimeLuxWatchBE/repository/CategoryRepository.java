package com.TimeLuxWatchBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.TimeLuxWatchBE.entity.CategoryEntity;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    
    /**
     * Tìm danh sách danh mục theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách danh mục
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.status = :status")
    List<CategoryEntity> findByStatus(@Param("status") int status);
    
    /**
     * Tìm danh sách danh mục theo tên (tìm gần đúng)
     * @param name Tên cần tìm
     * @return Danh sách danh mục
     */
    List<CategoryEntity> findByNameContaining(String name);
}
