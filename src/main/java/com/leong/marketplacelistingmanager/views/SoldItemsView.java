package com.leong.marketplacelistingmanager.views;

import com.leong.marketplacelistingmanager.entities.ItemEntity;
import com.leong.marketplacelistingmanager.services.ItemService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "sold")
public class SoldItemsView extends VerticalLayout {

    private Grid<ItemEntity> soldItemsGrid = new Grid<>(ItemEntity.class, false);
    private final ItemService itemService;


    public SoldItemsView(ItemService itemService) {
        this.itemService = itemService;
        RouterLink backButton =  new RouterLink("Vissza a kategóriákhoz", CategoryListView.class);

        H2 soldItemsHeader = new H2("Eladott termékek");
        soldItemsGrid.addColumn(ItemEntity::getTitle).setHeader("Név");
        soldItemsGrid.addItemDoubleClickListener(event ->
                UI.getCurrent().navigate(ItemView.class, event.getItem().getId()));

        add(soldItemsHeader, backButton, soldItemsGrid);
        refreshGrid();
    }

    private void refreshGrid(){
        soldItemsGrid.setItems(itemService.findSoldItem());
    }
}
