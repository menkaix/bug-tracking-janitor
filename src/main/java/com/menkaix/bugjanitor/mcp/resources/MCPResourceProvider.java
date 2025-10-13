package com.menkaix.bugjanitor.mcp.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.services.ProjectService;
import com.menkaix.bugjanitor.services.TaskService;
import com.menkaix.bugjanitor.models.documents.Project;
import com.menkaix.bugjanitor.models.documents.Task;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MCPResourceProvider {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final Gson jsonUtils;

    @Autowired
    public MCPResourceProvider(ProjectService projectService, TaskService taskService, Gson jsonUtils) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.jsonUtils = jsonUtils;
    }

    public Map<String, Object> getResource(String uri) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (uri.startsWith("projects/")) {
                return handleProjectResource(uri);
            } else if (uri.startsWith("tasks/")) {
                return handleTaskResource(uri);
            } else if (uri.startsWith("schemas/")) {
                return handleSchemaResource(uri);
            } else if (uri.startsWith("server/")) {
                return handleServerResource(uri);
            } else if (uri.startsWith("metrics/")) {
                return handleMetricsResource(uri);
            } else {
                response.put("error", "Unknown resource: " + uri);
                return response;
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> handleProjectResource(String uri) {
        Map<String, Object> response = new HashMap<>();
        
        if (uri.equals("projects")) {
            return getAllProjects();
        } else if (uri.startsWith("projects/") && uri.contains("/tasks")) {
            return getProjectTasks(uri);
        } else if (uri.startsWith("projects/")) {
            String projectId = uri.substring("projects/".length());
            return getProjectById(projectId);
        }
        
        response.put("error", "Invalid project resource: " + uri);
        return response;
    }

    private Map<String, Object> handleTaskResource(String uri) {
        Map<String, Object> response = new HashMap<>();
        
        if (uri.equals("tasks")) {
            return getAllTasks();
        } else if (uri.startsWith("tasks/by-tracking-ref/")) {
            String trackingRef = uri.substring("tasks/by-tracking-ref/".length());
            return getTaskByTrackingReference(trackingRef);
        } else if (uri.startsWith("tasks/by-status/")) {
            String status = uri.substring("tasks/by-status/".length());
            return getTasksByStatus(status);
        } else if (uri.equals("tasks/overdue")) {
            return getOverdueTasks();
        } else if (uri.equals("tasks/upcoming")) {
            return getUpcomingTasks();
        } else if (uri.startsWith("tasks/")) {
            String taskId = uri.substring("tasks/".length());
            return getTaskById(taskId);
        }
        
        response.put("error", "Invalid task resource: " + uri);
        return response;
    }

    private Map<String, Object> handleSchemaResource(String uri) {
        Map<String, Object> response = new HashMap<>();
        
        if (uri.equals("schemas/project")) {
            return getProjectSchema();
        } else if (uri.equals("schemas/task")) {
            return getTaskSchema();
        }
        
        response.put("error", "Invalid schema resource: " + uri);
        return response;
    }

    private Map<String, Object> handleServerResource(String uri) {
        Map<String, Object> response = new HashMap<>();
        
        if (uri.equals("server/health")) {
            response.put("status", "healthy");
            response.put("timestamp", new Date());
            return response;
        } else if (uri.equals("server/info")) {
            response.put("name", "Bug Tracking Janitor MCP Server");
            response.put("version", "1.0.0");
            response.put("capabilities", Arrays.asList("projects", "tasks", "schemas", "metrics"));
            return response;
        }
        
        response.put("error", "Invalid server resource: " + uri);
        return response;
    }

    private Map<String, Object> handleMetricsResource(String uri) {
        Map<String, Object> response = new HashMap<>();
        
        if (uri.equals("metrics/projects/count")) {
            long count = projectService.findAll(null, null, null).getTotalElements();
            response.put("count", count);
            return response;
        } else if (uri.equals("metrics/tasks/count")) {
            long count = taskService.findAll(null, null, null).getTotalElements();
            response.put("count", count);
            return response;
        }
        
        response.put("error", "Invalid metrics resource: " + uri);
        return response;
    }

    private Map<String, Object> getAllProjects() {
        Map<String, Object> response = new HashMap<>();
        try {
            var projects = projectService.findAll(null, null, null);
            response.put("projects", projects.getContent());
            response.put("totalElements", projects.getTotalElements());
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getProjectById(String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Project> project = projectService.findById(projectId);
            if (project.isPresent()) {
                response.put("project", project.get());
            } else {
                response.put("error", "Project not found: " + projectId);
            }
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getProjectTasks(String uri) {
        Map<String, Object> response = new HashMap<>();
        try {
            String[] parts = uri.split("/");
            if (parts.length >= 3) {
                String projectCode = parts[1];
                var tasks = taskService.findByProjectCode(projectCode);
                response.put("tasks", tasks);
                response.put("projectCode", projectCode);
            }
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getAllTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            var tasks = taskService.findAll(null, null, null);
            response.put("tasks", tasks.getContent());
            response.put("totalElements", tasks.getTotalElements());
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getTaskById(String taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Task> task = taskService.findById(taskId);
            if (task.isPresent()) {
                response.put("task", task.get());
            } else {
                response.put("error", "Task not found: " + taskId);
            }
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getTaskByTrackingReference(String trackingRef) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Task> task = taskService.findByTrackingReference(trackingRef);
            if (task.isPresent()) {
                response.put("task", task.get());
            } else {
                response.put("error", "Task not found with tracking reference: " + trackingRef);
            }
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getTasksByStatus(String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            var tasks = taskService.findByStatus(status);
            response.put("tasks", tasks);
            response.put("status", status);
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getOverdueTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            var tasks = taskService.findOverdueTasks();
            response.put("tasks", tasks);
            response.put("count", tasks.size());
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getUpcomingTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            var tasks = taskService.findUpcomingTasks();
            response.put("tasks", tasks);
            response.put("count", tasks.size());
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    private Map<String, Object> getProjectSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", Map.of("type", "string", "description", "Unique identifier"));
        properties.put("projectName", Map.of("type", "string", "description", "Display name of the project"));
        properties.put("projectCode", Map.of("type", "string", "description", "Unique code identifier"));
        properties.put("description", Map.of("type", "string", "description", "Project description"));
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("projectName", "projectCode"));
        return schema;
    }

    private Map<String, Object> getTaskSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", Map.of("type", "string", "description", "Unique identifier"));
        properties.put("projectCode", Map.of("type", "string", "description", "Associated project code"));
        properties.put("title", Map.of("type", "string", "description", "Task title"));
        properties.put("description", Map.of("type", "string", "description", "Task description"));
        properties.put("status", Map.of("type", "string", "description", "Task status"));
        properties.put("creationDate", Map.of("type", "string", "format", "date-time"));
        properties.put("updateDate", Map.of("type", "string", "format", "date-time"));
        properties.put("doneDate", Map.of("type", "string", "format", "date-time"));
        properties.put("plannedStart", Map.of("type", "string", "format", "date-time"));
        properties.put("deadLine", Map.of("type", "string", "format", "date-time"));
        properties.put("estimate", Map.of("type", "string", "description", "Time estimate"));
        properties.put("trackingReference", Map.of("type", "string", "description", "External tracking reference"));
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("title", "description"));
        return schema;
    }
}