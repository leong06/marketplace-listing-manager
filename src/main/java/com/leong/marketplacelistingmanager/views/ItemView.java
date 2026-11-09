package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.entities.ItemStatus;
import com.leong.marketplacelistingmanager.services.ItemService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route(value = "item")
public class ItemView extends VerticalLayout implements HasUrlParameter<Long> {
    private final TextField titleField = new TextField("Termék neve");
    private final Select<ItemStatus> statusSelect = new Select<>();
    private final TextArea descriptionField = new TextArea("Leírás");
    private final ItemService itemService;
    private ItemEntity currentItem;
    Binder<ItemEntity> binder = new Binder<>(ItemEntity.class);

    public ItemView(ItemService itemService) {
        this.itemService = itemService;
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


        add(backButton, titleField, statusSelect, descriptionField, saveButton, deleteButton);
        binder.forField(titleField).bind(ItemEntity::getTitle, ItemEntity::setTitle);
        binder.forField(statusSelect).bind(ItemEntity::getStatus, ItemEntity::setStatus);
        binder.forField(descriptionField).bind(ItemEntity::getDescription, ItemEntity::setDescription);
    }


    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.currentItem = itemService.findById(parameter);
        binder.readBean(currentItem);
    }
}
