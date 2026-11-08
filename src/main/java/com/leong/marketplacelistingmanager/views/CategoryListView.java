package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.services.CategoryService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Kategóriák")
public class CategoryListView extends VerticalLayout {
    private final CategoryService categoryService;
    private final Grid<CategoryEntity> categoryGrid = new Grid<>(CategoryEntity.class, false);

    public CategoryListView(CategoryService categoryService) {
        this.categoryService = categoryService;
        categoryGrid.addColumn(CategoryEntity::getName).setHeader("Név");

        add(new H2("Kategóriák"), categoryGrid);
        refreshGrid();
    }

    private void refreshGrid(){
        categoryGrid.setItems(categoryService.findAll());
    }

}
