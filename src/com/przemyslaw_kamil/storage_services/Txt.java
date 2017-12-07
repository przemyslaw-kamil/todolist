package com.przemyslaw_kamil.storage_services;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;

public class Txt implements StorageInterface { private static String filename = "todolist.txt";

    public void loadItems() {
        Path path = Paths.get(filename);
        String input;
        try (BufferedReader br = Files.newBufferedReader(path)){
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                long id = Long.parseLong(itemPieces[0]);
                String shortDescription = itemPieces[1];
                String details = itemPieces[2];
                LocalDate date = LocalDate.parse(itemPieces[3], formatter);
                ColorProject color = ColorProject.valueOf(itemPieces[4]);

                Item item = new Item(id, shortDescription, details, date, color);
                itemList.add(item);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void storeItems () {

        Path path = Paths.get(filename);

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            Iterator<Item> iter = itemList.iterator();
            while(iter.hasNext()){
                Item item = iter.next();
                bw.write(String.format("%d\t%s\t%s\t%s\t%s",
                        item.getId(),
                        item.getDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter),
                        item.getColorProject().name()
                ));
                bw.newLine();
            }

        }catch (IOException e){
            System.out.println(e.getMessage());

        }
    }

    public void deleteItem (Item item){
        StorageInterface.deleteItem(item);
    }
    public void addItem (Item item){
        StorageInterface.addItem(item);
    }
    public void  editItem (Item item, String description, String details, LocalDate deadline, ColorProject colorProject){
        StorageInterface.editItem(item, description,details,deadline, colorProject);
    }

    public void setProject(Item item, String color) {StorageInterface.setProject(item, color); }
}
