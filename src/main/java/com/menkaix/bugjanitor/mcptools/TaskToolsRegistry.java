package com.menkaix.bugjanitor.mcptools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskToolsRegistry {

    private final TaskServiceMCPTools taskServiceTools;
    private final Gson jsonUtils;

    @Autowired
    public TaskToolsRegistry(TaskServiceMCPTools tools, Gson jsonUtils) {
        taskServiceTools = tools;
        this.jsonUtils = jsonUtils;
    }

    @Tool(name = "create-task", description = "Creates a task from JSON. Fields available: title, description, projectCode, deadLine (ISO-8601), status")
    public String createTask(String taskJson) {
        try {
            Task task = taskServiceTools.createTask(taskJson);
            return jsonUtils.toJson(task);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-task-by-id", description = "Finds a task by its ID")
    public String findTaskById(String id) {
        try {
            Optional<Task> task = taskServiceTools.findTaskById(id);
            if (task.isPresent()) {
                return jsonUtils.toJson(task.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Tâche non trouvé avec l'ID: " + id);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "update-task", description = "Updates a task from JSON. The ID is required. Fields available: title, description, projectCode, deadLine (ISO-8601), status, doneDate (ISO-8601)")
    public String updateTask(String taskDetails) {
        try {
            Task task = taskServiceTools.updateTask(taskDetails);
            return jsonUtils.toJson(task);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "delete-task", description = "Deletes a task by its ID")
    public String deleteTask(String id) {
        try {
            String result = taskServiceTools.deleteTask(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return jsonUtils.toJson(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-tasks", description = "Finds tasks with pagination. Parameters: page (default 0), size (default 10), search (optional), filter (optional in format 'key:value')")
    public String findTasks(int page, int size, String search, String filter) {
        try {
            if (page < 0)
                page = 0;
            if (size <= 0)
                size = 10;
            if (size > 100)
                size = 100;

            Pageable pageable = PageRequest.of(page, size);
            Page<Task> tasks = taskServiceTools.findAllTasks(pageable, search, filter);

            Map<String, Object> result = new HashMap<>();
            result.put("content", tasks.getContent());
            result.put("totalElements", tasks.getTotalElements());
            result.put("totalPages", tasks.getTotalPages());
            result.put("currentPage", tasks.getNumber());
            result.put("size", tasks.getSize());
            result.put("hasNext", tasks.hasNext());
            result.put("hasPrevious", tasks.hasPrevious());

            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }
}