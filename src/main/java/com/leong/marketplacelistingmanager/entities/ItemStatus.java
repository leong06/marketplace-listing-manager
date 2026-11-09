package com.leong.marketplacelistingmanager.entities;

import lombok.Getter;

@Getter
public enum ItemStatus {
    NOT_LISTED("Nincs meghirdetve"),
    LISTING("Aktív hirdetés"),
    SOLD("Eladva");

    private final String label;

    ItemStatus(String label) {
        this.label = label;
    }

}
