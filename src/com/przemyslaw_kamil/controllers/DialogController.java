package com.przemyslaw_kamil.controllers;

import com.przemyslaw_kamil.datamodel.entities.ColorProject;
import com.przemyslaw_kamil.datamodel.Data;
import com.przemyslaw_kamil.datamodel.entities.Item;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Optional;

public class DialogController {

    @FXML
    private ToggleGroup colorProjectGroup;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    public boolean isValid(){

        StringBuilder sb = new StringBuilder();
        if(descriptionField.getText().trim().length()==0) {sb.append("\ndescription");}
        if(detailsArea.getText().trim().length()==0) {sb.append("\ndetails");}
        if (deadlinePicker.getValue() == null) {sb.append("\ndeadline");}

        if(sb.length()>0){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeight(800.0);
            alert.setTitle("Warning");
            alert.setHeaderText("Please complete all fields");
            alert.setContentText("Please complete:" + sb.toString());
            Optional <ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
            }
            return false;
        }
        return true;
    }

    public Item processResults() {
        String shortDescription = descriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();
        ColorProject colorProject = ColorProject.valueOf(colorProjectGroup.getSelectedToggle().getUserData().toString());

        Item newItem = new Item(shortDescription, details, deadlineValue, colorProject);
        Data.getInstance().addItem(newItem);
        return newItem;

    }

    public void editItem(Item item){
        descriptionField.setText(item.getDescription());
        detailsArea.setText(item.getDetails());
        setColorProjectGroup(item.getColorName());
        deadlinePicker.setValue(item.getDeadline());
    }

    public void updateItem(Item item){
        String shortDescription = descriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadline = deadlinePicker.getValue();
        ColorProject colorProject = ColorProject.valueOf(colorProjectGroup.getSelectedToggle().getUserData().toString());

        Data.getInstance().editItem(item,shortDescription, details, deadline, colorProject);
    }

    private void setColorProjectGroup(String color) {
        ObservableList<Toggle> toggleList = (colorProjectGroup.getToggles());
        for(Toggle toggle: toggleList){
            if (toggle.getUserData().toString().equals(color)){
                colorProjectGroup.selectToggle(toggle);
                return;
            }
        }
    }

}
