package com.przemyslaw_kamil.datamodel;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;
import com.przemyslaw_kamil.storage_services.Sqlite;
import com.przemyslaw_kamil.storage_services.Txt;
import com.przemyslaw_kamil.storage_services.Xml;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class Data {

    private static Data instance = new Data();


    ////////////////////////////////////////////////////
////  There are three ways to save data:
////  text file, xml file, or database sqlite
////
////     private static Xml data = new Xml();
    private static Sqlite data=new Sqlite();
////  private static Txt data = new Txt();
    /////////////////////////////////////////////////////

    public ObservableList<Item> getItems() {
        return data.getItemList();
    }

    public static Data getInstance() {
        return instance;
    }

    public  void storeItems() {
        data.storeItems();
    }

    public void loadItems() {
        data.loadItems();
    }

    public void addItem(Item item){
        data.addItem(item);
        storeItems();
    }

    public void editItem (Item item, String description, String details, LocalDate deadline, ColorProject colorProject) {
        data.editItem(item, description, details, deadline, colorProject);
        storeItems();
    }
    public void deleteItem(Item item){
        data.deleteItem(item);
        storeItems();
    };
    public void setProject (Item item, String color){
       data.setProject(item, color);
        storeItems();
    };



}
