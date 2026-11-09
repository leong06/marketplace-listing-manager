package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.services.CategoryService;
import com.leong.marketplacelistingmanager.services.ItemService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("category")
public class CategoryView extends VerticalLayout implements HasUrlParameter<Long> {

    private final CategoryService categoryService;
    private final ItemService itemService;
    private Long categoryId;
    private final H2 titleHeader = new H2();
    private final Grid<ItemEntity> itemGrid = new Grid<>(ItemEntity.class, false);

    public CategoryView(CategoryService categoryService, ItemService itemService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
        itemGrid.addColumn(ItemEntity::getTitle).setHeader("Termék Név");
        itemGrid.addColumn(item -> item.getStatus().getLabel()).setHeader("Státusz");
        RouterLink backButton =  new RouterLink("Vissza a kategóriákhoz", CategoryListView.class);
        Button addItem = new Button("Új elem hozzáadása", event -> createDialog());
        add(titleHeader, backButton, addItem, itemGrid);

    }

    private void createDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Új termék");

        TextField nameField = new TextField("Termék neve");
        nameField.setMaxLength(200);
        nameField.setWidthFull();
        dialog.add(nameField);

        Button saveButton = new Button("Mentés",event -> {
            try{
                itemService.create(categoryId, nameField.getValue());
                refreshGrid();
                dialog.close();
                Notification.show("Termék sikeresen létrehozva");
            } catch (IllegalArgumentException e){
                Notification.show("Hiba történt a termék létrehozása során: " + e.getMessage());
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

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.categoryId = parameter;
        try {
            CategoryEntity category = categoryService.findById(categoryId);
            if (category != null) {
                titleHeader.setText(category.getName());
                refreshGrid();
            } else {
                Notification.show("Nem található a kategória!");
                event.rerouteTo(CategoryListView.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshGrid(){
        itemGrid.setItems(itemService.findByCategory(categoryId));
    }



}
