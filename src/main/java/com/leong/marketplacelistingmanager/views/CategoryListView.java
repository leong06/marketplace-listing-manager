package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.services.CategoryService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
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
        categoryGrid.addComponentColumn(item -> {
            Button deleteButton = new Button("❌", event -> openDeleteDialog(item));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return deleteButton;
        });

        Button addButton = new Button("Új kategória", event -> openAddDialog());
        Button soldItemsButton = new Button("Eladott termékek", event -> UI.getCurrent().navigate(SoldItemsView.class));
        add(new H2("Kategóriák"), addButton, soldItemsButton, categoryGrid);

        categoryGrid.addItemDoubleClickListener(event ->
                UI.getCurrent().navigate(CategoryView.class, event.getItem().getId()));

        setMaxWidth("1100px");
        setWidthFull();
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "1.5rem 2rem");

        refreshGrid();
    }

    private void openDeleteDialog(CategoryEntity category) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Kategória törlése");
        dialog.add(new Span("Biztosan törölni szeretné a(z) " + category.getName() + " kategóriát?"));

        Button confirmDeleteButton = new Button("Törlés", event -> {
           try{
               categoryService.deleteById(category.getId());
               refreshGrid();
               dialog.close();
               Notification.show("A kategória sikeresen törölve!");
           } catch (IllegalArgumentException e){
               Notification.show(e.getMessage());
               dialog.close();
           }
        });
        confirmDeleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelDeleteButton = new Button("Mégsem", event -> dialog.close());
        cancelDeleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.getFooter().add(confirmDeleteButton, cancelDeleteButton);
        dialog.open();
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
