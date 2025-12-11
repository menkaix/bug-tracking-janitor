package com.menkaix.bugjanitor.mcptools;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Task;
import com.menkaix.bugjanitor.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceMCPTools {

    private final TaskService taskService;
    private final Gson jsonUtils;

    @Autowired
    public TaskServiceMCPTools(TaskService taskService, Gson jsonUtils) {
        this.taskService = taskService;
        this.jsonUtils = jsonUtils;
    }

    public Task createTask(String taskJson) {
        try {
            if (taskJson == null || taskJson.trim().isEmpty()) {
                throw new IllegalArgumentException("Le JSON de la tâche ne peut pas être null ou vide");
            }

            Task task = jsonUtils.fromJson(taskJson, Task.class);

            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom de la tâche est requis");
            }

            return taskService.create(task);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la tâche: " + e.getMessage(), e);
        }
    }

    public Optional<Task> findTaskById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID de la tâche ne peut pas être null ou vide");
            }
            return taskService.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de la tâche: " + e.getMessage(), e);
        }
    }

    public Optional<Task> findTaskByTrackingReference(String trackingReference) {
        try {
            if (trackingReference == null || trackingReference.trim().isEmpty()) {
                throw new IllegalArgumentException("La référence de suivi ne peut pas être null ou vide");
            }
            return taskService.findByTrackingReference(trackingReference);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de la tâche par référence de suivi: " + e.getMessage(), e);
        }
    }

    public Task updateTask(String taskJson) {
        try {
            if (taskJson == null || taskJson.trim().isEmpty()) {
                throw new IllegalArgumentException("Le JSON de la tâche ne peut pas être null ou vide");
            }

            Task taskDetails = jsonUtils.fromJson(taskJson, Task.class);

            if (taskDetails.getId() == null || taskDetails.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID de la tâche est requis pour la mise à jour");
            }

            Task existingTask = taskService.findById(taskDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvé avec l'ID: " + taskDetails.getId()));

            if (taskDetails.getTitle() != null) {
                existingTask.setTitle(taskDetails.getTitle());
            }
            if (taskDetails.getDescription() != null) {
                existingTask.setDescription(taskDetails.getDescription());
            }
            if (taskDetails.getAssignee() != null) {
                existingTask.setAssignee(taskDetails.getAssignee());
            }

            return taskService.update(existingTask);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la tâche: " + e.getMessage(), e);
        }
    }

    public String deleteTask(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID de la tâche ne peut pas être null ou vide");
            }

            taskService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tâche non trouvé avec l'ID: " + id));

            taskService.delete(id);
            return "Tâche supprimé avec succès: " + id;
        } catch (Exception e) {
            return "Erreur lors de la suppression: " + e.getMessage();
        }
    }

    public Page<Task> findAllTasks(Pageable pageable, String search, String filter) {
        try {
            return taskService.findAll(pageable, search, filter);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des tâches: " + e.getMessage(), e);
        }
    }

    public List<Task> findOverdueTasks() {
        try {
            return taskService.findOverdueTasks();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des tâches en retard: " + e.getMessage(), e);
        }
    }

    public List<Task> findUpcomingTasks() {
        try {
            return taskService.findUpcomingTasks();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des tâches à venir: " + e.getMessage(), e);
        }
    }

    public List<Task> findTasksByStatus(String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                throw new IllegalArgumentException("Le statut ne peut pas être null ou vide");
            }
            return taskService.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des tâches par statut: " + e.getMessage(), e);
        }
    }

    public List<Task> findTasksByProjectCode(String projectCode) {
        try {
            if (projectCode == null || projectCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Le code du projet ne peut pas être null ou vide");
            }
            return taskService.findByProjectCode(projectCode);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des tâches par projet: " + e.getMessage(), e);
        }
    }
}