package com.przemyslaw_kamil.storage_services;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

abstract class StorageService implements StorageInterface {
    static ObservableList<Item> itemList = FXCollections.observableArrayList();

    public ObservableList<Item> getItemList(){return itemList;}

    public void deleteItem (Item item){
        itemList.remove(item);
    }
    public  void addItem(Item item){
        itemList.add(item);
    }
    public  void setProject (Item item, String color){
        item.setProject(color);
    }
    public void  editItem (Item item, String description, String details, LocalDate deadline, ColorProject colorProject){
        item.editItem(description,details,deadline, colorProject);
    }
}
