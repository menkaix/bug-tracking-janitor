package com.menkaix.bugjanitor.mcptools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectToolsRegistry {

    private final ProjectServiceMCPTools projectServiceTools;
    private final Gson jsonUtils;

    @Autowired
    public ProjectToolsRegistry(ProjectServiceMCPTools tools, Gson jsonUtils) {
        projectServiceTools = tools;
        this.jsonUtils = jsonUtils;
    }

    @Tool(name = "create-project", description = "Creates a new project from JSON input. Required fields: projectName (string, display name), projectCode (string, unique identifier), description (string, project details). Returns the created project with auto-generated ID. Example: {\"projectName\":\"Bug Tracker\",\"projectCode\":\"BT001\",\"description\":\"Main bug tracking system\"}")
    public String createProject(String projectJson) {
        try {
            Project project = projectServiceTools.createProject(projectJson);
            return jsonUtils.toJson(project);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-project-by-id", description = "Retrieves a specific project by its unique identifier. Parameter: id (string, required, project identifier). Returns the complete project object if found, or an error message if the project doesn't exist.")
    public String findProjectById(String id) {
        try {
            Optional<Project> project = projectServiceTools.findProjectById(id);
            if (project.isPresent()) {
                return jsonUtils.toJson(project.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Projet non trouvé avec l'ID: " + id);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "update-project", description = "Updates an existing project with new data from JSON input. Required field: id (string, project identifier). Updatable fields: projectName (string, display name), projectCode (string, unique identifier), description (string, project details). Returns the updated project. Example: {\"id\":\"507f1f77bcf86cd799439011\",\"projectName\":\"Updated Name\",\"projectCode\":\"BT002\",\"description\":\"Updated description\"}")
    public String updateProject(String projectDetails) {
        try {
            Project project = projectServiceTools.updateProject(projectDetails);
            return jsonUtils.toJson(project);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "delete-project", description = "Permanently removes a project from the system. Parameter: id (string, required, project identifier). Returns a confirmation message upon successful deletion or an error if the project doesn't exist. Warning: This action cannot be undone.")
    public String deleteProject(String id) {
        try {
            String result = projectServiceTools.deleteProject(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return jsonUtils.toJson(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-projects", description = "Retrieves projects with pagination, search, and filtering capabilities. Parameters: page (int, default 0, zero-based page number), size (int, default 10, max 100, number of items per page), search (string, optional, searches in projectName and description), filter (string, optional, format 'fieldName:value' for exact matching). Returns paginated results with project list, total count, page info, and navigation flags.")
    public String findProjects(int page, int size, String search, String filter) {
        try {
            // Valeurs par défaut
            if (page < 0)
                page = 0;
            if (size <= 0)
                size = 10;
            if (size > 100)
                size = 100; // Limite maximale

            Pageable pageable = PageRequest.of(page, size);
            Page<Project> projects = projectServiceTools.findAllProjects(pageable, search, filter);

            Map<String, Object> result = new HashMap<>();
            result.put("content", projects.getContent());
            result.put("totalElements", projects.getTotalElements());
            result.put("totalPages", projects.getTotalPages());
            result.put("currentPage", projects.getNumber());
            result.put("size", projects.getSize());
            result.put("hasNext", projects.hasNext());
            result.put("hasPrevious", projects.hasPrevious());

            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-project-by-code", description = "Retrieves a project by its unique project code. Parameter: projectCode (string, required, project code identifier). Returns the complete project object if found, or an error message if no project exists with the specified code.")
    public String findProjectByCode(String projectCode) {
        try {
            Optional<Project> project = projectServiceTools.findProjectByCode(projectCode);
            if (project.isPresent()) {
                return jsonUtils.toJson(project.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Aucun projet trouvé avec le code: " + projectCode);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-project-by-name", description = "Retrieves a project by its project name. Parameter: projectName (string, required, project name). Returns the complete project object if found, or an error message if no project exists with the specified name.")
    public String findProjectByName(String projectName) {
        try {
            Optional<Project> project = projectServiceTools.findProjectByName(projectName);
            if (project.isPresent()) {
                return jsonUtils.toJson(project.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Aucun projet trouvé avec le nom: " + projectName);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }
}
