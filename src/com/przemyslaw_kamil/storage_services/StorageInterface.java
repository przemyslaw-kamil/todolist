package com.przemyslaw_kamil.storage_services;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface StorageInterface {

    ObservableList<Item> itemList = FXCollections.observableArrayList();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static ObservableList<Item> getItemList(){return itemList;}
    void loadItems();
    void storeItems ();

    static void deleteItem (Item item){
        itemList.remove(item);
    }
    static void addItem(Item item){
        itemList.add(item);
    }
    static void setProject (Item item, String color){
        item.setProject(color);
    }
    static void  editItem (Item item, String description, String details, LocalDate deadline, ColorProject colorProject){
        item.editItem(description,details,deadline, colorProject);
    }

}
