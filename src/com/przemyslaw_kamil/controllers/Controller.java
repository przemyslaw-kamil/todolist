package com.przemyslaw_kamil.controllers;


import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.Data;
import com.przemyslaw_kamil.datamodel.entities.Item;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private VBox rightVbox;

    @FXML
    private VBox dateVbox;
    @FXML
    private ObservableList <Item> listItem;

    @FXML
    private FilteredList <Item> filteredList;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ListView <Item> mainListView;

    @FXML
    private ListView <Item> greenListView;

    @FXML
    private ListView <Item> redListView;
    @FXML
    private ListView <Item> projectsListViewYellow;
    @FXML
    private ListView <Item> projectsListViewBlue;
    @FXML
    private ListView <Item> projectsListViewOrange;
    @FXML
    private ListView <Item> projectsListViewNone;

    @FXML
    private TextArea itemDetailTextArea;

    @FXML
    private Label deadlineLabel;

    @FXML
    private Label colorLabel;

    @FXML
    private ToggleButton projectsButton;

    @FXML
    private ToggleGroup viewSearch;
    @FXML
    private ToggleGroup viewFilter;

    @FXML
    private ToggleButton listItems;

    @FXML
    private CheckBox searchCheckBox;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox <String> filterComboBox;

    @FXML
    private DatePicker dateFrom;

    @FXML
    private DatePicker dateTo;

    private Map <String, ListView <Item>> mapListView = new HashMap <>();

    private Map <String, FilteredList <Item>> mapFilteredList = new HashMap <>();

    private Map <String, Predicate <Item>> mapColorPredicate = new HashMap <>();
    private Map <String, Predicate <Item>> mapFilterPredicate = new HashMap <>();
    private Comparator <Item> comparatorDeadline = (((o1, o2) -> o1.getDeadline().compareTo(o2.getDeadline())));

    public void initialize() {
        mainBorderPane.setRight(null);

        listItem = Data.getInstance().getItems();

        mapListView.put(ColorProject.Red.name(), redListView);
        mapListView.put(ColorProject.Blue.name(), projectsListViewBlue);
        mapListView.put(ColorProject.Green.name(), greenListView);
        mapListView.put(ColorProject.Orange.name(), projectsListViewOrange);
        mapListView.put(ColorProject.Yellow.name(), projectsListViewYellow);
        mapListView.put(ColorProject.none.name(), projectsListViewNone);

        mapFilterPredicate.put("All", (e -> true));
        mapFilterPredicate.put("Filter", (e -> true));
        mapFilterPredicate.put("Today", (e -> e.getDeadline().isEqual(LocalDate.now())));
        mapFilterPredicate.put("Week", (e -> e.getDeadline().isAfter(LocalDate.now()) && e.getDeadline().isBefore(LocalDate.now().plusWeeks(1))));
        mapFilterPredicate.put("Month", (e -> e.getDeadline().isAfter(LocalDate.now()) && e.getDeadline().isBefore(LocalDate.now().plusMonths(1))));
        mapFilterPredicate.put("Date...", e -> true);


        SortedList <Item> sortedList = new SortedList <Item>(listItem, comparatorDeadline);
        filteredList = new FilteredList <Item>(sortedList, (e -> true));

        mainListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener <Item>() {
            @Override
            public void changed(ObservableValue <? extends Item> observable, Item oldValue, Item newValue) {
                if (newValue != null) {
                    Item item = mainListView.getSelectionModel().getSelectedItem();
                    itemDetailTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                    colorLabel.setText(item.getColorName());
                    colorLabel.setStyle("-fx-background-color:" + item.getColorName().toLowerCase());
                    try {
                        mapListView.get(oldValue.getColorName()).getSelectionModel().clearSelection();
                    } catch (NullPointerException e) {
                    }
                    mapListView.get(item.getColorName()).getSelectionModel().select(item);
                } else {
                    itemDetailTextArea.setText("");
                    deadlineLabel.setText("");
                    colorLabel.setText("");
                    colorLabel.setStyle("-fx-background-color: lightgray");
                }
            }
        });

        ChangeListener <Item> projectViewListener = new ChangeListener <Item>() {
            @Override
            public void changed(ObservableValue <? extends Item> observable, Item oldValue, Item newValue) {
                if (newValue != null) {
                    if (oldValue != null && !(oldValue.getColorName().equals(newValue.getColorName()))) {
                    }
                    mainListView.getSelectionModel().select(newValue);
                }

            }

        };
        Callback <ListView <Item>, ListCell <Item>> callback = new Callback <ListView <Item>, ListCell <Item>>() {
            @Override
            public ListCell <Item> call(ListView <Item> param) {
                ListCell <Item> cell = new ListCell <Item>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getDescription());
                            if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }

                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;

            }
        };

        EventHandler <MouseEvent> doubleclick = click -> {
            if (click.getClickCount() == 2) {
                editItemDialog();
            }
        };
        mainListView.setItems(sortedList);
        mainListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mainListView.getSelectionModel().selectFirst();
        mainListView.setCellFactory(callback);
        mainListView.setOnMouseClicked(doubleclick);

        for (ColorProject color : ColorProject.values()) {
            mapColorPredicate.put(color.toString(), e -> e.getColorProject() == color);
            mapFilteredList.put(color.toString(), new FilteredList <Item>(sortedList, mapColorPredicate.get(color.toString())));
            ListView <Item> listView = mapListView.get(color.toString());
            listView.setItems(mapFilteredList.get(color.toString()));
            listView.getSelectionModel().selectedItemProperty().addListener(projectViewListener);
            listView.setCellFactory(callback);
            listView.setOnMouseClicked(doubleclick);
        }

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(action -> deleteItem());
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(action -> editItemDialog());

        Menu projectMenu = new Menu("Change project");
        MenuItem redProject = new MenuItem ("Red");
        redProject.setOnAction(action -> changeProject(ColorProject.Red.toString()));
        MenuItem greenProject = new MenuItem ("Green");
        greenProject.setOnAction(action -> changeProject(ColorProject.Green.toString()));
        MenuItem blueProject = new MenuItem ("Blue");
        blueProject.setOnAction(action -> changeProject(ColorProject.Blue.toString()));
        MenuItem yellowProject = new MenuItem ("Yellow");
        yellowProject.setOnAction(action -> changeProject(ColorProject.Yellow.toString()));
        MenuItem orangeProject = new MenuItem ("Orange");
        orangeProject.setOnAction(action -> changeProject(ColorProject.Orange.toString()));
        MenuItem noneProject = new MenuItem ("none");
        noneProject.setOnAction(action -> changeProject(ColorProject.none.toString()));

        projectMenu.getItems().addAll(redProject, greenProject, blueProject,yellowProject,orangeProject,noneProject);


        listContextMenu.getItems().addAll(deleteMenuItem, editMenuItem, projectMenu);
        dateTo.setDayCellFactory(new Callback <DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (dateFrom.getValue() != null && item.isBefore(dateFrom.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: lightgray");
                        }
                    }
                };
            }
        });

        dateFrom.setDayCellFactory(new Callback <DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (dateTo.getValue() != null && item.isAfter(dateTo.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb");
                        }
                    }
                };
            }
        });

    }

    @FXML
    public void newItemDialog() {
        Dialog <ButtonType> dialog = new Dialog <>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new To-Do item");
        dialog.setHeaderText("Use this dialog to create a new To-Do item");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("itemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController controller = fxmlLoader.getController();
        Button buttonOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        buttonOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (!controller.isValid()) {
                event.consume();
            }
        });
        Optional <ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Item newItem = controller.processResults();
            mainListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    private void editItemDialog() {
        Item item = mainListView.getSelectionModel().getSelectedItem();

        Dialog <ButtonType> dialog = new Dialog <>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit To-Do item");
        dialog.setHeaderText("Use this dialog to edit To-Do item");


        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("itemDialog.fxml"));


        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        DialogController editController = fxmlLoader.getController();
        editController.editItem(item);
        String color1 = item.getColorName();
        Button buttonOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        buttonOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (!editController.isValid()) {
                event.consume();
            }
        });
        Optional <ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            editController.updateItem(item);
            String color2 = item.getColorName();
            refreshColorListView(color1, color2);
            mainListView.refresh();
            mainListView.getSelectionModel().clearSelection();
            mainListView.getSelectionModel().select(item);
        }
    }

    @FXML
    public void showProjects() {
        if (projectsButton.isSelected()) {
            mainBorderPane.setRight(rightVbox);

        } else {
            mainBorderPane.setRight(null);
        }
    }

    @FXML
    public void showList() {
        if (listItems.isSelected()) {
            mainBorderPane.getLeft().setStyle("");
        } else {
            mainBorderPane.getLeft().setStyle("-fx-max-height: 0; -fx-max-width: 0");
        }
    }

    @FXML
    public void changeProject(String color){
        Item item = mainListView.getSelectionModel().getSelectedItem();

        String colorBefore = item.getColorProject().toString();
        Data.getInstance().setProject(item,color);
        refreshColorListView(colorBefore,color);
        mainListView.refresh();
        mainListView.getSelectionModel().clearSelection();
        mainListView.getSelectionModel().select(item);
    }

    @FXML
    private void refreshColorListView(String c1, String c2) {

        if (c1.equals(c2)) {
            mapListView.get(c1).refresh();
            return;
        }
        FilteredList <Item> list = mapFilteredList.get(c1);
        Predicate <? super Item> pred = list.getPredicate();

        list.setPredicate(e -> false);
        list.setPredicate(pred);
        mapListView.get(c1).setItems(list);

        list = mapFilteredList.get(c2);
        pred= list.getPredicate();

        list.setPredicate(e -> false);
        list.setPredicate(pred);
        mapListView.get(c2).setItems(list);
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }


    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.DELETE) && !searchField.isFocused()) {
            deleteItem();
        }
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (searchField.isFocused()) {
                searchItems();
            } else {
                editItemDialog();
            }
        }
    }

    @FXML
    public void clickRed() {
        visibleProject(ColorProject.Red.toString());
    }

    @FXML
    public void clickGreen() {
        visibleProject(ColorProject.Green.toString());
    }

    @FXML
    public void clickBlue() {
        visibleProject(ColorProject.Blue.toString());
    }

    @FXML
    public void clickYellow() {
        visibleProject(ColorProject.Yellow.toString());
    }

    @FXML
    public void clickOrange() {
        visibleProject(ColorProject.Orange.toString());
    }

    @FXML
    public void clickNone() {
        visibleProject(ColorProject.none.toString());
    }


    @FXML
    private void visibleProject(String color) {
        ListView <Item> listView = mapListView.get(color);
        if (listView.isVisible()) {
            listView.setVisible(false);
            listView.setStyle("-fx-max-height: 0; -fx-max-width: 0");

        } else {
            listView.setVisible(true);
            listView.setStyle("");

        }
    }

    @FXML
    private void deleteItem() {
        Item item = mainListView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(mainBorderPane.getScene().getWindow());
        alert.setHeight(800.0);
        alert.setTitle("Delete item");
        String headerText = (item.getDescription().length() < 30 ? item.getDescription() : item.getDescription().substring(0, 29));
        alert.setHeaderText("Delete: " + headerText + "?");
        alert.setContentText("Delete this item?");
        Optional <ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Data.getInstance().deleteItem(item);
        }
    }

    @FXML
    private void searchItems() {
        if (searchField.getLength() == 0) {
            mainListView.setItems(filteredList);
            mainListView.refresh();
            for (ColorProject color : ColorProject.values()) {
                mapListView.get(color.toString()).setItems(mapFilteredList.get(color.toString()));
                mapListView.get(color.toString()).refresh();
            }
            return;
        }
        Predicate <Item> searchPredicate = searchPredicate();

        switch (viewSearch.getSelectedToggle().getUserData().toString()) {
            case "leftView":

                mainListView.setItems(new FilteredList <Item>(filteredList,
                        searchPredicate));
                mainListView.refresh();

                for (ColorProject color : ColorProject.values()) {
                    mapListView.get(color.toString()).setItems(mapFilteredList.get(color.toString()));
                    mapListView.get(color.toString()).refresh();
                    mapListView.get(color.toString()).getSelectionModel().clearSelection();

                }
                if (!mainListView.getItems().isEmpty()) {
                    mainListView.getSelectionModel().selectFirst();
                }
                break;

            case "rightView":
                mainListView.setItems(filteredList);
                mainListView.refresh();
                boolean isSelected = false;
                for (ColorProject color : ColorProject.values()) {
                    ListView <Item> listView = mapListView.get(color.toString());
                    listView.setItems
                            (new FilteredList <Item>(mapFilteredList.get(color.toString()), searchPredicate));
                    listView.refresh();
                    if (!isSelected && !listView.getSelectionModel().isEmpty()) {
                        listView.getSelectionModel().selectFirst();
                        isSelected = true;
                    }
                }

                break;

            case "bothView":
                mainListView.setItems(new FilteredList <Item>(filteredList,
                        searchPredicate));
                mainListView.refresh();
                mainListView.getSelectionModel().selectFirst();
                for (ColorProject color : ColorProject.values()) {
                    mapListView.get(color.toString()).setItems
                            (new FilteredList <Item>(mapFilteredList.get(color.toString()), searchPredicate));
                    mapListView.get(color.toString()).refresh();
                }
                break;
        }
    }

    public Predicate <Item> searchPredicate() {
        String keywords = searchField.getText().trim().toLowerCase();

        if (keywords.length() == 0) {
            return null;
        }

        if (searchCheckBox.isSelected()) {
            return (e -> e.getDescription().toLowerCase().contains(keywords) || e.getDetails().contains(keywords));
        } else {
            return (e -> e.getDescription().toLowerCase().contains(keywords));
        }
    }

    public void setFilteredList() {
        if (filterComboBox.getSelectionModel().getSelectedItem().equals("Date...")) {
            dateVbox.setVisible(true);

            if (dateTo.getValue() != null) {
                if (dateFrom.getValue() != null) {
                    mapFilterPredicate.put("Date...", e -> (e.getDeadline().isBefore(dateTo.getValue().plusDays(1)) && e.getDeadline().isAfter(dateFrom.getValue().minusDays(1))));
                } else {
                    mapFilterPredicate.put("Date...", e -> e.getDeadline().isBefore(dateTo.getValue().plusDays(1)));
                }
            } else if (dateFrom.getValue() != null) {
                mapFilterPredicate.put("Date...", e -> e.getDeadline().isAfter(dateFrom.getValue().minusDays(1)));
            } else {
                mapFilterPredicate.put("Date...", e -> true);
            }
        } else {
            dateVbox.setVisible(false);
        }


        switch (viewFilter.getSelectedToggle().getUserData().toString()) {
            case "leftView":
                filteredList.setPredicate(mapFilterPredicate.get(filterComboBox.getSelectionModel().getSelectedItem()));
                mainListView.refresh();

                for (ColorProject color : ColorProject.values()) {
                    mapFilteredList.get(color.toString()).setPredicate(mapColorPredicate.get(color.toString()));
                    mapListView.get(color.toString()).refresh();
                }
                mainListView.getSelectionModel().selectFirst();
                break;

            case "rightView":
                filteredList.setPredicate(e -> true);
                mainListView.refresh();

                for (ColorProject color : ColorProject.values()) {
                    mapFilteredList.get(color.toString()).setPredicate(mapColorPredicate.get(color.toString()).and(mapFilterPredicate.get(filterComboBox.getSelectionModel().getSelectedItem())));
                    mapListView.get(color.toString()).refresh();
                    if (mainListView.getSelectionModel().getSelectedItem() == null) {
                        mapListView.get(color.toString()).getSelectionModel().selectFirst();
                    }
                }
                break;

            case "bothView":
                for (ColorProject color : ColorProject.values()) {
                    mapFilteredList.get(color.toString()).setPredicate(mapColorPredicate.get(color.toString()).and(mapFilterPredicate.get(filterComboBox.getSelectionModel().getSelectedItem())));
                    mapListView.get(color.toString()).refresh();
                }

                filteredList.setPredicate(mapFilterPredicate.get(filterComboBox.getSelectionModel().getSelectedItem()));
                mainListView.refresh();
                mainListView.getSelectionModel().selectFirst();

                break;
        }
        searchItems();
    }

}