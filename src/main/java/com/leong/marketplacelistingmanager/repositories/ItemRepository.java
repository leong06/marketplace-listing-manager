package com.leong.marketplacelistingmanager.repositories;

import com.leong.marketplacelistingmanager.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity>
    findByCategoryIdOrderByTitleAsc(Long categoryId);
}
