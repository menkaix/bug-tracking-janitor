package com.menkaix.bugjanitor.mcp.prompts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import java.util.*;

@Service
public class MCPPromptService {

    private final MCPPromptTemplates promptTemplates;
    private final Gson jsonUtils;

    @Autowired
    public MCPPromptService(MCPPromptTemplates promptTemplates, Gson jsonUtils) {
        this.promptTemplates = promptTemplates;
        this.jsonUtils = jsonUtils;
    }

    public Map<String, Object> getPrompt(String name) {
        String prompt = promptTemplates.getPrompt(name);
        Map<String, Object> response = new HashMap<>();
        
        if (prompt != null) {
            response.put("name", name);
            response.put("description", getPromptDescription(name));
            response.put("content", prompt);
            response.put("arguments", getPromptArguments(name));
        } else {
            response.put("error", "Prompt not found: " + name);
        }
        
        return response;
    }

    public Map<String, Object> getAllPrompts() {
        Map<String, String> allPrompts = promptTemplates.getAllPrompts();
        Map<String, Object> response = new HashMap<>();
        
        List<Map<String, Object>> promptList = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : allPrompts.entrySet()) {
            Map<String, Object> promptInfo = new HashMap<>();
            promptInfo.put("name", entry.getKey());
            promptInfo.put("description", getPromptDescription(entry.getKey()));
            promptInfo.put("category", getPromptCategory(entry.getKey()));
            promptInfo.put("arguments", getPromptArguments(entry.getKey()));
            promptList.add(promptInfo);
        }
        
        response.put("prompts", promptList);
        response.put("total", promptList.size());
        
        return response;
    }

    public Map<String, Object> getPromptsByCategory(String category) {
        Map<String, String> allPrompts = promptTemplates.getAllPrompts();
        Map<String, Object> response = new HashMap<>();
        
        List<Map<String, Object>> filteredPrompts = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : allPrompts.entrySet()) {
            if (getPromptCategory(entry.getKey()).equalsIgnoreCase(category)) {
                Map<String, Object> promptInfo = new HashMap<>();
                promptInfo.put("name", entry.getKey());
                promptInfo.put("description", getPromptDescription(entry.getKey()));
                promptInfo.put("arguments", getPromptArguments(entry.getKey()));
                filteredPrompts.add(promptInfo);
            }
        }
        
        response.put("category", category);
        response.put("prompts", filteredPrompts);
        response.put("total", filteredPrompts.size());
        
        return response;
    }

    public Map<String, Object> getPromptForRole(String role) {
        String prompt = promptTemplates.getPromptForRole(role);
        Map<String, Object> response = new HashMap<>();
        
        response.put("role", role);
        response.put("description", "Prompt optimisé pour le rôle: " + role);
        response.put("content", prompt);
        response.put("recommended_tools", getRecommendedToolsForRole(role));
        
        return response;
    }

    public Map<String, Object> getPromptForWorkflow(String workflow) {
        String prompt = promptTemplates.getPromptForWorkflow(workflow);
        Map<String, Object> response = new HashMap<>();
        
        response.put("workflow", workflow);
        response.put("description", "Prompt optimisé pour le workflow: " + workflow);
        response.put("content", prompt);
        response.put("recommended_tools", getRecommendedToolsForWorkflow(workflow));
        
        return response;
    }

    public Map<String, Object> getPromptCategories() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, List<String>> categories = new HashMap<>();
        categories.put("roles", Arrays.asList("project-manager", "developer", "qa-tester"));
        categories.put("workflows", Arrays.asList("sprint-planning", "daily-standup", "bug-triage"));
        categories.put("technical", Arrays.asList("data-validation", "reporting"));
        categories.put("system", Arrays.asList("system-orchestrator"));
        
        response.put("categories", categories);
        response.put("total_categories", categories.size());
        
        return response;
    }

    private String getPromptDescription(String name) {
        return switch (name) {
            case "system-orchestrator" -> "Prompt principal pour l'orchestration système et les règles générales";
            case "project-manager" -> "Assistant spécialisé pour les gestionnaires de projet";
            case "developer" -> "Assistant optimisé pour les développeurs et tâches techniques";
            case "qa-tester" -> "Assistant dédié aux testeurs et validation qualité";
            case "sprint-planning" -> "Workflow de planification de sprint et estimation";
            case "daily-standup" -> "Assistant pour les réunions quotidiennes d'équipe";
            case "bug-triage" -> "Processus de triage et priorisation des bugs";
            case "data-validation" -> "Validation et contrôle de cohérence des données";
            case "reporting" -> "Génération de rapports et métriques";
            default -> "Prompt personnalisé pour " + name;
        };
    }

    private String getPromptCategory(String name) {
        if (Arrays.asList("project-manager", "developer", "qa-tester").contains(name)) {
            return "roles";
        } else if (Arrays.asList("sprint-planning", "daily-standup", "bug-triage").contains(name)) {
            return "workflows";
        } else if (Arrays.asList("data-validation", "reporting").contains(name)) {
            return "technical";
        } else {
            return "system";
        }
    }

    private List<String> getPromptArguments(String name) {
        return switch (name) {
            case "sprint-planning" -> Arrays.asList("sprint_duration", "team_capacity", "project_priorities");
            case "daily-standup" -> Arrays.asList("date", "team_members", "focus_areas");
            case "bug-triage" -> Arrays.asList("severity_levels", "project_scope", "assignee_list");
            case "reporting" -> Arrays.asList("date_range", "metrics_type", "output_format");
            default -> Arrays.asList();
        };
    }

    private List<String> getRecommendedToolsForRole(String role) {
        return switch (role.toLowerCase()) {
            case "project-manager", "pm", "manager" -> Arrays.asList(
                "find-overdue-tasks", "find-upcoming-tasks", "find-tasks-by-project", 
                "find-projects", "metrics/projects/count", "metrics/tasks/count"
            );
            case "developer", "dev", "programmer" -> Arrays.asList(
                "find-tasks-by-status", "find-tasks-by-project", "update-task", 
                "create-task", "find-task-by-id"
            );
            case "qa-tester", "qa", "tester" -> Arrays.asList(
                "find-task-by-tracking-reference", "find-tasks-by-status", 
                "update-task", "create-task", "find-tasks-by-project"
            );
            default -> Arrays.asList(
                "find-projects", "find-tasks", "create-project", "create-task"
            );
        };
    }

    private List<String> getRecommendedToolsForWorkflow(String workflow) {
        return switch (workflow.toLowerCase()) {
            case "sprint-planning", "sprint", "planning" -> Arrays.asList(
                "find-overdue-tasks", "find-upcoming-tasks", "find-tasks-by-status", 
                "find-tasks-by-project", "update-task"
            );
            case "daily-standup", "standup", "daily" -> Arrays.asList(
                "find-tasks-by-status", "find-overdue-tasks", "find-upcoming-tasks"
            );
            case "bug-triage", "triage", "bug" -> Arrays.asList(
                "create-task", "find-task-by-tracking-reference", "find-project-by-code", 
                "update-task", "find-tasks-by-status"
            );
            case "data-validation", "validation", "check" -> Arrays.asList(
                "find-project-by-code", "find-task-by-id", "find-task-by-tracking-reference"
            );
            case "reporting", "report" -> Arrays.asList(
                "find-tasks", "find-projects", "metrics/projects/count", 
                "metrics/tasks/count", "find-tasks-by-status"
            );
            default -> Arrays.asList("find-projects", "find-tasks");
        };
    }
}