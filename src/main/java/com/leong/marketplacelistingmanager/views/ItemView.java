package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.entities.ItemStatus;
import com.leong.marketplacelistingmanager.services.ItemService;
import com.leong.marketplacelistingmanager.services.PhotoStorageService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.clipboard.Clipboard;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.UploadHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Route(value = "item")
public class ItemView extends VerticalLayout implements HasUrlParameter<Long> {
    private final TextField titleField = new TextField("Termék neve");
    private final Select<ItemStatus> statusSelect = new Select<>();
    private final TextArea descriptionField = new TextArea("Leírás");
    private final ItemService itemService;

    private ItemEntity currentItem;
    private PhotoStorageService photoStorageService;
    private final HorizontalLayout photoGallery = new HorizontalLayout();

    private final HorizontalLayout topToolbar = new HorizontalLayout();
    private final HorizontalLayout statusBar = new HorizontalLayout();
    private final HorizontalLayout bottomToolbar = new HorizontalLayout();

    Binder<ItemEntity> binder = new Binder<>(ItemEntity.class);

    public ItemView(ItemService itemService, PhotoStorageService photoStorageService) {
        this.itemService = itemService;
        this.photoStorageService = photoStorageService;
        statusSelect.setItems(ItemStatus.values());
        statusSelect.setItemLabelGenerator(ItemStatus::getLabel);

        Button backButton = new Button("Vissza", event ->{
            UI.getCurrent().navigate(CategoryView.class, currentItem.getCategory().getId());
        });

        Button saveButton = new Button("Mentés", event ->{
            if(binder.writeBeanIfValid(currentItem)){
                itemService.update(
                        currentItem.getId(),
                        currentItem.getTitle(),
                        currentItem.getDescription(),
                        currentItem.getStatus()
                );
                Notification.show("Sikeres mentés!");
            }
        });

        Button deleteButton = new Button("Törlés", event ->{
            Long categoryId = currentItem.getCategory().getId();
            itemService.deleteById(currentItem.getId());

            Notification.show("Sikeres törlés!");
            UI.getCurrent().navigate(CategoryView.class, categoryId);
        });

        Button openFolderButton = new Button("Mappa megnyitása", event ->{
            try{
                photoStorageService.openFolder(currentItem);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button copyDescription = new Button("Leírás másolása", event ->{
            UI.getCurrent().getPage().executeJs(
                    "navigator.clipboard.writeText($0)",
                    descriptionField.getValue()
            );
            Notification.show("Leírás a vágólapra másolva");
        });
        //Clipboard.onClick(copyDescription).writeText(descriptionField.getValue());

        //Style
        setMaxWidth("1100px");
        setWidthFull();
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "1.5rem 2rem");

        topToolbar.add(backButton, openFolderButton);

        statusSelect.setLabel("Hirdetés státusza:");
        statusBar.add(titleField, statusSelect);
        titleField.setWidthFull();
        statusBar.setWidthFull();

        descriptionField.setWidthFull();
        descriptionField.setMaxHeight("180px");

        bottomToolbar.add(saveButton, copyDescription, deleteButton);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Upload upload = uploadButton();

        add(topToolbar, photoGallery, upload, statusBar, descriptionField, bottomToolbar);

        binder.forField(titleField).bind(ItemEntity::getTitle, ItemEntity::setTitle);
        binder.forField(statusSelect).bind(ItemEntity::getStatus, ItemEntity::setStatus);
        binder.forField(descriptionField).bind(ItemEntity::getDescription, ItemEntity::setDescription);
    }

    private Upload uploadButton(){
        Upload upload = new Upload(UploadHandler.inMemory(((metadata, data) ->
                photoStorageService.savePhoto(currentItem.getId(), metadata.fileName(), new ByteArrayInputStream(data)))));
        upload.setAcceptedFileTypes("image/*");
        upload.setUploadButton(new Button("Képek feltöltése"));
        upload.addAllFinishedListener(event -> {
           this.currentItem = getCurrentItem(currentItem.getId());
           refreshPhotoGallery(currentItem.getId());
           upload.clearFileList();
        });

        return upload;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.currentItem = itemService.findById(parameter);
        binder.readBean(currentItem);

        photoGallery.setWrap(true);
        refreshPhotoGallery(parameter);
    }

    private void refreshPhotoGallery(Long parameter){
        currentItem = getCurrentItem(parameter);
        photoGallery.removeAll();
        try {
            for (Path photoPath: photoStorageService.listPhotos(currentItem)){
                Image img = new Image(
                        DownloadHandler.forFile(photoPath.toFile()),
                        photoPath.getFileName().toString()
                );

                Span caption = new Span(photoPath.getFileName().toString());
                caption.addClassName("photo-card-label");

                Div card = new Div(img, caption);
                card.addClassName("photo-card");


                img.setHeight("500px");
                photoGallery.add(card);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private ItemEntity getCurrentItem(Long parameter){
        return itemService.findById(parameter);
    }


}
