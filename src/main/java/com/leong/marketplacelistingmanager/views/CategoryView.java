package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.CategoryEntity;
import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.entities.ItemStatus;
import com.leong.marketplacelistingmanager.services.CategoryService;
import com.leong.marketplacelistingmanager.services.ItemService;
import com.leong.marketplacelistingmanager.services.PhotoStorageService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.streams.UploadHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Route("category")
public class CategoryView extends VerticalLayout implements HasUrlParameter<Long> {

    private final CategoryService categoryService;
    private final ItemService itemService;
    private final PhotoStorageService photoStorageService;
    private Long categoryId;
    private final H2 titleHeader = new H2();
    private final Grid<ItemEntity> itemGrid = new Grid<>(ItemEntity.class, false);

    public CategoryView(CategoryService categoryService, ItemService itemService, PhotoStorageService photoStorageService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
        this.photoStorageService = photoStorageService;
        itemGrid.addColumn(ItemEntity::getTitle).setHeader("Termék Név");
        itemGrid.addColumn(item -> item.getStatus().getLabel()).setHeader("Státusz");
        RouterLink backButton =  new RouterLink("Vissza a kategóriákhoz", CategoryListView.class);
        Button addItem = new Button("Új elem hozzáadása", event -> createDialog());
        itemGrid.addItemDoubleClickListener(event ->
                UI.getCurrent().navigate(ItemView.class, event.getItem().getId()));
        add(titleHeader, backButton, addItem, itemGrid);

        itemGrid.setPartNameGenerator(item ->
                item.getStatus() == ItemStatus.LISTING ? "active-listing" : null);

    }

    private void createDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Új termék");

        TextField nameField = new TextField("Termék neve");
        nameField.setMaxLength(200);
        nameField.setWidthFull();
        Map<String, byte[]> uploadedFiles = new LinkedHashMap<>();
        Upload upload = new Upload(UploadHandler.inMemory((metadata, data) -> {
            uploadedFiles.put(metadata.fileName(), data);
        }));
        upload.setAcceptedFileTypes("image/*");
        dialog.add(nameField, upload);

        Button saveButton = new Button("Mentés",event -> {
            try{
                ItemEntity createdItem = itemService.create(categoryId, nameField.getValue());
                for (var entry : uploadedFiles.entrySet()) {
                    photoStorageService.savePhoto(
                            createdItem.getId(),
                            entry.getKey(),
                            new ByteArrayInputStream(entry.getValue())
                    );
                }
                refreshGrid();
                dialog.close();
                Notification.show("Termék sikeresen létrehozva");
            } catch (IllegalArgumentException e){
                Notification.show("Hiba történt a termék létrehozása során: " + e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
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
