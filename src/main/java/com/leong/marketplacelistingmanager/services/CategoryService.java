package com.leong.marketplacelistingmanager.services;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.repositories.CategoryRepository;

import com.leong.marketplacelistingmanager.repositories.ItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    public List<CategoryEntity> findAll(){
        return categoryRepository.findAll(Sort.by("name"));
    }


    @Transactional(readOnly = true)
    public CategoryEntity findById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public CategoryEntity rename(Long id, String newName){
        String trimName = newName == null ? "" : newName.trim();
        if (trimName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        CategoryEntity category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setName(trimName);
        return categoryRepository.save(category);
    }


    @Transactional
    public CategoryEntity create(String name){
        String trimName = name == null ? "" : name.trim();
        if (trimName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        if (categoryRepository.existsByNameIgnoreCase(trimName)){
            throw new IllegalArgumentException("Category already exists");
        }

        CategoryEntity category = new CategoryEntity(trimName);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteById(Long id){
        if(!itemRepository.findByCategoryIdOrderByTitleAsc(id).isEmpty()){
            throw new IllegalArgumentException("Nem törölhető nem üres kategória!");
        }
        categoryRepository.deleteById(id);

    }


}
