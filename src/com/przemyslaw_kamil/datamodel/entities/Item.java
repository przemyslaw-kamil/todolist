package com.przemyslaw_kamil.datamodel.entities;

import java.time.LocalDate;

public class Item {
    private long id;
    private String description;
    private String details;
    private LocalDate deadline;
    private ColorProject colorProject;
    private static long idCount = 0;


    public Item(long id, String description, String details, LocalDate deadline, ColorProject colorProject) {
        this.id = id;
        this.description = description;
        this.details = details;
        this.deadline = deadline;
        this.colorProject = colorProject;
        if (id > idCount) idCount = id;
    }

    public Item(String description, String details, LocalDate deadline, ColorProject colorProject) {
        this.description = description;
        this.details = details;
        this.deadline = deadline;
        this.colorProject = colorProject;
        this.id = ++idCount;
    }

    public void editItem(String description, String details, LocalDate deadline, ColorProject colorProject) {
        this.description = description;
        this.details = details;
        this.deadline = deadline;
        this.colorProject = colorProject;
    }


    public void setProject (String color){
        this.colorProject=ColorProject.valueOf(color);
    }


    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDetails() {
        return details;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public ColorProject getColorProject() {
        return colorProject;
    }

    public String getColorName() {
        return colorProject.name();
    }


}
