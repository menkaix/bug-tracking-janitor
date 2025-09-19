package com.menkaix.bugjanitor.mcptools;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Project;
import com.menkaix.bugjanitor.services.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class ProjectServiceMCPTools {

    private final ProjectService projectService;
    private final Gson jsonUtils;

    @Autowired
    public ProjectServiceMCPTools(ProjectService projectService, Gson jsonUtils) {
        this.projectService = projectService;
        this.jsonUtils = jsonUtils;
    }

    public Project createProject(String projectJson) {
        try {
            if (projectJson == null || projectJson.trim().isEmpty()) {
                throw new IllegalArgumentException("Le JSON du projet ne peut pas être null ou vide");
            }

            Project project = jsonUtils.fromJson(projectJson, Project.class);

            // Validation basique
            if (project.getProjectName() == null || project.getProjectName().trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom du projet est requis");
            }
            if (project.getProjectCode() == null || project.getProjectCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Le code du projet est requis");
            }

            return projectService.create(project);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du projet: " + e.getMessage(), e);
        }
    }

    public Optional<Project> findProjectById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID du projet ne peut pas être null ou vide");
            }
            return projectService.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche du projet: " + e.getMessage(), e);
        }
    }

    public Project updateProject(String projectJson) {
        try {
            if (projectJson == null || projectJson.trim().isEmpty()) {
                throw new IllegalArgumentException("Le JSON du projet ne peut pas être null ou vide");
            }

            Project projectDetails = jsonUtils.fromJson(projectJson, Project.class);

            if (projectDetails.getId() == null || projectDetails.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID du projet est requis pour la mise à jour");
            }

            // Vérifier que le projet existe
            Project existingProject = projectService.findById(projectDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + projectDetails.getId()));

            // Mettre à jour les champs
            if (projectDetails.getProjectName() != null) {
                existingProject.setProjectName(projectDetails.getProjectName());
            }
            if (projectDetails.getProjectCode() != null) {
                existingProject.setProjectCode(projectDetails.getProjectCode());
            }
            if (projectDetails.getDescription() != null) {
                existingProject.setDescription(projectDetails.getDescription());
            }

            return projectService.update(existingProject);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du projet: " + e.getMessage(), e);
        }
    }

    public String deleteProject(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID du projet ne peut pas être null ou vide");
            }

            // Vérifier que le projet existe avant de le supprimer
            projectService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + id));

            projectService.delete(id);
            return "Projet supprimé avec succès: " + id;
        } catch (Exception e) {
            return "Erreur lors de la suppression: " + e.getMessage();
        }
    }

    public Page<Project> findAllProjects(Pageable pageable, String search, String filter) {
        try {
            return projectService.findAll(pageable, search, filter);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des projets: " + e.getMessage(), e);
        }
    }

    public Optional<Project> findProjectByCode(String projectCode) {
        try {
            if (projectCode == null || projectCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Le code du projet ne peut pas être null ou vide");
            }
            return projectService.findByProjectCode(projectCode);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche du projet par code: " + e.getMessage(), e);
        }
    }

    public Optional<Project> findProjectByName(String projectName) {
        try {
            if (projectName == null || projectName.trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom du projet ne peut pas être null ou vide");
            }
            return projectService.findByProjectName(projectName);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche du projet par nom: " + e.getMessage(), e);
        }
    }
}
