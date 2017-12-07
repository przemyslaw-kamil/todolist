package com.przemyslaw_kamil.storage_services;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.entities.Item;

import java.sql.*;
import java.time.LocalDate;

public class Sqlite implements StorageInterface {

    private static final String DB_FILE = "todolist.db";

    private static String CONNECTION_STRING = "jdbc:sqlite:" + DB_FILE;

    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEMS_ID = "_id";
    private static final String COLUMN_ITEMS_DESCRIPTION = "description";
    private static final String COLUMN_ITEMS_DETAILS = "details";
    private static final String COLUMN_ITEMS_DEADLINE = "deadline";
    private static final String COLUMN_ITEMS_COLOR_PROJECT = "project";
    private static final int INDEX_ID = 1;
    private static final int INDEX_DESCRIPTION = 2;
    private static final int INDEX_DETAILS = 3;
    private static final int INDEX_DEADLINE = 4;
    private static final int INDEX_COLOR_PROJECT = 5;

    private static final String INSERT_TO_DO_ITEMS = "INSERT  INTO " + TABLE_ITEMS +
            "(" + COLUMN_ITEMS_ID + ", " + COLUMN_ITEMS_DESCRIPTION + ", " + COLUMN_ITEMS_DETAILS + ", " +
            COLUMN_ITEMS_DEADLINE + ", " + COLUMN_ITEMS_COLOR_PROJECT + ") VALUES(?, ?, ?, ?, ?)";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_ITEMS + "(" + COLUMN_ITEMS_ID + " INTEGER, " +
            COLUMN_ITEMS_DESCRIPTION + " TEXT NOT NULL, " + COLUMN_ITEMS_DETAILS + " TEXT NOT NULL, " + COLUMN_ITEMS_DEADLINE + " TEXT NOT NULL, " +
            COLUMN_ITEMS_COLOR_PROJECT + " TEXT NOT NULL);";

    private static final String EDIT_TO_DO_ITEM = "UPDATE " + TABLE_ITEMS + " SET " +
            COLUMN_ITEMS_DESCRIPTION + " = ? , " + COLUMN_ITEMS_DETAILS + " = ? , " +
            COLUMN_ITEMS_DEADLINE + " = ? , " + COLUMN_ITEMS_COLOR_PROJECT + "= ? WHERE " + COLUMN_ITEMS_ID + " = ?";

    private static final String DELETE_TO_DO_ITEM = "DELETE FROM " + TABLE_ITEMS +
            " WHERE " + COLUMN_ITEMS_ID + " = ?";

    private static final String QUERY_ALL_TO_DO_ITEMS = "SELECT * FROM " + TABLE_ITEMS;


    public void loadItems() {

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(QUERY_ALL_TO_DO_ITEMS)) {

            long id = -1;
            String description;
            String details;
            LocalDate deadline;
            ColorProject colorProject;

            while (results.next()) {
                id = results.getLong(INDEX_ID);
                description = results.getString(INDEX_DESCRIPTION);
                details = results.getString(INDEX_DETAILS);
                deadline = LocalDate.parse(results.getString(INDEX_DEADLINE), formatter);
                colorProject = ColorProject.valueOf(results.getString(INDEX_COLOR_PROJECT));

                Item item = new Item(id, description, details, deadline, colorProject);
                itemList.add(item);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                createTable();
                loadItems();
            } catch (SQLException e1) {
                System.out.println("SQL connection failed" + e1.getMessage());
            }
        }
    }

    @Override
    public void storeItems() {}

    private void createTable() throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement statement = conn.createStatement()) {
            statement.execute(CREATE_TABLE);

        } catch (SQLException e) {
            throw new SQLException("Can't create Table");

        }
    }

    public void editItem(Item item, String description, String details, LocalDate deadline, ColorProject colorProject) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement statement = conn.prepareStatement(EDIT_TO_DO_ITEM)) {
            statement.setString(1, description);
            statement.setString(2, details);
            statement.setString(3, deadline.format(formatter));
            statement.setString(4, colorProject.toString());
            statement.setLong(5, item.getId());

            statement.execute();

            StorageInterface.editItem(item, description, details, deadline, colorProject);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteItem(Item item) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement statement = conn.prepareStatement(DELETE_TO_DO_ITEM)) {
            statement.setLong(1, item.getId());
            statement.execute();
            StorageInterface.deleteItem(item);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean addTDItem(Item item) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement statement = conn.prepareStatement(INSERT_TO_DO_ITEMS)) {

            statement.setLong(1, item.getId());
            statement.setString(2, item.getDescription());
            statement.setString(3, item.getDetails());
            statement.setString(4, item.getDeadline().format(formatter));
            statement.setString(5, item.getColorProject().toString());

            statement.execute();
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void addItem(Item item) {
        if (addTDItem(item)) {
            StorageInterface.addItem(item);
        } else {
            System.out.println("Can't add item to database");
        }

    }

    public void setProject(Item item, String color) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement statement = conn.prepareStatement(EDIT_TO_DO_ITEM)) {
            statement.setString(1, item.getDescription());
            statement.setString(2, item.getDetails());
            statement.setString(3, item.getDeadline().format(formatter));
            statement.setString(4, color);
            statement.setLong(5, item.getId());

            statement.execute();

            StorageInterface.setProject(item, color);
        } catch (SQLException e) {
            System.out.println("Can't change project \n" + e.getMessage());
        }
    }
}