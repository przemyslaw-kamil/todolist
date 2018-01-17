package com.przemyslaw_kamil.storage_services;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

 interface StorageInterface {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    ObservableList<Item> getItemList();
    void loadItems();
    void storeItems ();

    void deleteItem(Item item);
    void addItem(Item item);
    void setProject (Item item, String color);
    void editItem (Item item, String description, String details, LocalDate deadline, ColorProject colorProject);

}
