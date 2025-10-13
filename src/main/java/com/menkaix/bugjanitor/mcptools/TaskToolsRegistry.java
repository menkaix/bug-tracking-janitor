package com.menkaix.bugjanitor.mcptools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskToolsRegistry {

    private final TaskServiceMCPTools taskServiceTools;
    private final Gson jsonUtils;

    public TaskToolsRegistry(TaskServiceMCPTools tools, Gson jsonUtils) {
        taskServiceTools = tools;
        this.jsonUtils = jsonUtils;
    }

    @Tool(name = "create-task", description = "Creates a new task from JSON input. Required fields: title (string), description (string). Optional fields: projectCode (string, must reference existing project), plannedStart (ISO-8601 date), deadLine (ISO-8601 date), status (string), estimate (string), trackingReference (string, external tracking ID). Returns the created task with auto-generated ID and timestamps.")
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

    @Tool(name = "find-task-by-id", description = "Retrieves a specific task by its unique identifier. Parameter: id (string, required). Returns the complete task object if found, or an error message if the task doesn't exist.")
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

    @Tool(name = "find-task-by-tracking-reference", description = "Retrieves a specific task by its tracking reference. Parameter: trackingReference (string, required, external tracking identifier). Returns the complete task object if found, or an error message if no task with that tracking reference exists.")
    public String findTaskByTrackingReference(String trackingReference) {
        try {
            Optional<Task> task = taskServiceTools.findTaskByTrackingReference(trackingReference);
            if (task.isPresent()) {
                return jsonUtils.toJson(task.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Tâche non trouvé avec la référence de suivi: " + trackingReference);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "update-task", description = "Updates an existing task with new data from JSON input. Required field: id (string, task identifier). Updatable fields: title (string), description (string), projectCode (string, must reference existing project), plannedStart (ISO-8601 date), deadLine (ISO-8601 date), status (string), doneDate (ISO-8601 date), estimate (string), trackingReference (string, external tracking ID). Returns the updated task with refreshed updateDate timestamp.")
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

    @Tool(name = "delete-task", description = "Permanently removes a task from the system. Parameter: id (string, required, task identifier). Returns a confirmation message upon successful deletion or an error if the task doesn't exist.")
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

    @Tool(name = "find-tasks", description = "Retrieves tasks with pagination, search, and filtering capabilities. Parameters: page (int, default 0, zero-based page number), size (int, default 10, max 100, number of items per page), search (string, optional, searches in title and description), filter (string, optional, format 'fieldName:value' for exact matching). Returns paginated results with task list, total count, page info, and navigation flags.")
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

    @Tool(name = "find-overdue-tasks", description = "Retrieves all tasks that are overdue (deadline has passed and task is not completed). Returns a list of tasks with their details including deadline dates. No parameters required.")
    public String findOverdueTasks() {
        try {
            List<Task> overdueTasks = taskServiceTools.findOverdueTasks();
            Map<String, Object> result = new HashMap<>();
            result.put("tasks", overdueTasks);
            result.put("count", overdueTasks.size());
            result.put("message", "Tâches en retard trouvées");
            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-upcoming-tasks", description = "Retrieves all tasks with deadlines in the next 7 days that are not yet completed. Useful for planning and prioritization. Returns a list of tasks with their deadline information. No parameters required.")
    public String findUpcomingTasks() {
        try {
            List<Task> upcomingTasks = taskServiceTools.findUpcomingTasks();
            Map<String, Object> result = new HashMap<>();
            result.put("tasks", upcomingTasks);
            result.put("count", upcomingTasks.size());
            result.put("message", "Tâches à venir dans les 7 prochains jours");
            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-tasks-by-status", description = "Retrieves all tasks with a specific status. Parameter: status (string, required, task status to filter by). Common statuses include 'TODO', 'IN_PROGRESS', 'DONE', 'BLOCKED', etc. Returns a list of tasks matching the specified status.")
    public String findTasksByStatus(String status) {
        try {
            List<Task> tasks = taskServiceTools.findTasksByStatus(status);
            Map<String, Object> result = new HashMap<>();
            result.put("tasks", tasks);
            result.put("count", tasks.size());
            result.put("status", status);
            result.put("message", "Tâches trouvées avec le statut: " + status);
            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-tasks-by-project", description = "Retrieves all tasks associated with a specific project. Parameter: projectCode (string, required, project code to filter tasks by). Returns a list of tasks belonging to the specified project with their complete details.")
    public String findTasksByProject(String projectCode) {
        try {
            List<Task> tasks = taskServiceTools.findTasksByProjectCode(projectCode);
            Map<String, Object> result = new HashMap<>();
            result.put("tasks", tasks);
            result.put("count", tasks.size());
            result.put("projectCode", projectCode);
            result.put("message", "Tâches trouvées pour le projet: " + projectCode);
            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }
}