package com.menkaix.bugjanitor.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Project {

    @Id
    private String id;
    private String projectName;
    private String projectCode;
    private String description;

    // Constructeur par défaut
    public Project() {
    }

    // Constructeur avec paramètres
    public Project(String projectName, String projectCode, String description) {
        this.projectName = projectName;
        this.projectCode = projectCode;
        this.description = description;
    }

    // Constructeur complet
    public Project(String id, String projectName, String projectCode, String description) {
        this.id = id;
        this.projectName = projectName;
        this.projectCode = projectCode;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectCode='" + projectCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
