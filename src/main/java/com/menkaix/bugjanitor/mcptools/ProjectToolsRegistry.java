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

    @Tool(name = "create-project", description = "Creates a project from JSON. Expected format: {\"projectName\":\"name\",\"projectCode\":\"code\",\"description\":\"desc\"}")
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

    @Tool(name = "find-project-by-id", description = "Finds a project by its ID")
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

    @Tool(name = "update-project", description = "Updates a project from JSON. Expected format: {\"id\":\"projectId\",\"projectName\":\"name\",\"projectCode\":\"code\",\"description\":\"desc\"}")
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

    @Tool(name = "delete-project", description = "Deletes a project by its ID")
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

    @Tool(name = "find-projects", description = "Finds projects with pagination. Parameters: page (default 0), size (default 10), search (optional), filter (optional in format 'key:value')")
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
}
