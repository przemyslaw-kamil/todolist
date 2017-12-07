package com.przemyslaw_kamil.storage_services;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

public class Xml implements StorageInterface {

    private static final String DATA_FILE= "todolist.xml";
    private static final String ITEM="item";
    private static final String ITEM_ID= "_id";
    private static final String DESCRIPTION = "description";
    private static final String DETAILS="details";
    private static final String DEADLINE="deadline";
    private static final String COLOR_PROJECT ="color_project";

    public void loadItems() {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(DATA_FILE);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            long id=-1;
            String description=null;
            String details=null;
            LocalDate deadline=null;
            ColorProject colorProject=null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals(ITEM)) {
                        continue;
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart()
                                .equals(ITEM_ID)) {
                            event = eventReader.nextEvent();
                            id = Long.parseLong(event.asCharacters().getData());
                            continue;
                        }
                    }
                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DESCRIPTION)) {
                        event = eventReader.nextEvent();
                        description= event.asCharacters().getData();
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DETAILS)) {
                        event = eventReader.nextEvent();
                        details = event.asCharacters().getData();
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DEADLINE)) {
                        event = eventReader.nextEvent();
                        deadline = LocalDate.parse(event.asCharacters().getData(),formatter);
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart()
                            .equals(COLOR_PROJECT)) {
                        event = eventReader.nextEvent();
                        colorProject = ColorProject.valueOf(event.asCharacters().getData());
                        continue;
                    }
                }

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(ITEM)) {
                        Item tdItem = new Item(id,description,details,deadline,colorProject);
                        itemList.add(tdItem);
                        id=-1;
                        description=null;
                        details=null;
                        deadline=null;
                        colorProject=null;
                    }
                }
            }
        }
        catch (FileNotFoundException | XMLStreamException e) {
            System.out.println(e.getMessage());
        }
    }

    public void storeItems () {

        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(DATA_FILE));
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(end);

            StartElement tdItemsStartElement = eventFactory.createStartElement("",
                    "", "ToDoItems");
            eventWriter.add(tdItemsStartElement);
            eventWriter.add(end);

            for (Item item: itemList) {
                saveItem(eventWriter, eventFactory, item);
            }

            eventWriter.add(eventFactory.createEndElement("", "", "ToDoItems"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        }
        catch (XMLStreamException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveItem(XMLEventWriter eventWriter, XMLEventFactory eventFactory, Item item)
            throws FileNotFoundException, XMLStreamException {

        XMLEvent end = eventFactory.createDTD("\n");

        StartElement configStartElement = eventFactory.createStartElement("",
                "", ITEM);
        eventWriter.add(configStartElement);
        eventWriter.add(end);
        createNode(eventWriter,ITEM_ID, String.valueOf(item.getId()));
        createNode(eventWriter, DESCRIPTION, item.getDescription());
        createNode(eventWriter, DETAILS, item.getDetails());
        createNode(eventWriter, DEADLINE, item.getDeadline().format(formatter));
        createNode(eventWriter, COLOR_PROJECT, item.getColorProject().toString());

        eventWriter.add(eventFactory.createEndElement("", "", ITEM));
        eventWriter.add(end);
    }

    private void createNode(XMLEventWriter eventWriter, String name,
                            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }

    public void  editItem (Item item, String description, String details, LocalDate deadline, ColorProject colorProject){
        StorageInterface.editItem(item, description,details,deadline, colorProject);
    }

    public void deleteItem (Item item){
            StorageInterface.deleteItem(item);
    }
    public void addItem (Item item){
            StorageInterface.addItem(item);
    }
    public void setProject(Item item, String color) {StorageInterface.setProject(item, color);
    }
}
