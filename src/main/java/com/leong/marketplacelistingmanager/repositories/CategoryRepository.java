package com.leong.marketplacelistingmanager.repositories;


import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
