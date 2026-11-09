package com.leong.marketplacelistingmanager.services;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.entities.ItemStatus;
import com.leong.marketplacelistingmanager.repositories.CategoryRepository;
import com.leong.marketplacelistingmanager.repositories.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ItemEntity> findByCategory(Long categoryId){
        return itemRepository.findByCategoryIdOrderByTitleAsc(categoryId);
    }
    @Transactional
    public ItemEntity findById(Long id){
        ItemEntity item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item does not exist"));
        item.getCategory().getName();
        return item;
    }

    @Transactional
    public ItemEntity create(Long categoryId, String title){
        String trimTitle = title == null ? "" : title.trim();
        if (trimTitle.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        CategoryEntity category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            throw new IllegalArgumentException("Category does not exist");
        }

        ItemEntity item = new ItemEntity(trimTitle, category);
        return itemRepository.save(item);
    }

    @Transactional
    public ItemEntity update(Long id, String title, String description, ItemStatus status){
        String trimTitle = title == null ? "" : title.trim();
        if (trimTitle.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        
        ItemEntity item = findById(id);

        item.setTitle(trimTitle);
        item.setDescription(description);
        item.setStatus(status);
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteById(Long id){
        itemRepository.deleteById(id);
    }


}
