package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.services.CategoryService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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

        Button addButton = new Button("Új kategória", event -> openAddDialog());

        add(new H2("Kategóriák"), addButton, categoryGrid);
        refreshGrid();
    }

    private void openAddDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Új kategória");

        TextField nameField = new TextField("Név");
        nameField.setMaxLength(50);
        nameField.setWidthFull();
        dialog.add(nameField);

        Button saveButton = new Button("Mentés",event -> {
            try{
                categoryService.create(nameField.getValue());
                refreshGrid();
                dialog.close();
                Notification.show("Kategória sikeresen létrehozva");
            } catch (IllegalArgumentException e){
                Notification.show("Hiba történt a kategória létrehozása során: " + e.getMessage());
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);

        Button cancelButton = new Button("Mégsem", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickShortcut(Key.ESCAPE);
        dialog.getFooter().add(saveButton, cancelButton);
        dialog.open();
        nameField.focus();
    }

    private void refreshGrid(){
        categoryGrid.setItems(categoryService.findAll());
    }

}
