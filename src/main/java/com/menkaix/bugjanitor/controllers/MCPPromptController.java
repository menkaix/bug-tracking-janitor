package com.menkaix.bugjanitor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.menkaix.bugjanitor.mcp.prompts.MCPPromptService;

import java.util.Map;

@RestController
@RequestMapping("/mcp/prompts")
public class MCPPromptController {

    private final MCPPromptService promptService;

    @Autowired
    public MCPPromptController(MCPPromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPrompts() {
        try {
            Map<String, Object> result = promptService.getAllPrompts();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving prompts: " + e.getMessage()));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Map<String, Object>> getPrompt(@PathVariable String name) {
        try {
            Map<String, Object> result = promptService.getPrompt(name);
            
            if (result.containsKey("error")) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving prompt: " + e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getPromptCategories() {
        try {
            Map<String, Object> result = promptService.getPromptCategories();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving categories: " + e.getMessage()));
        }
    }

    @GetMapping("/categories/{category}")
    public ResponseEntity<Map<String, Object>> getPromptsByCategory(@PathVariable String category) {
        try {
            Map<String, Object> result = promptService.getPromptsByCategory(category);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving prompts by category: " + e.getMessage()));
        }
    }

    @GetMapping("/roles/{role}")
    public ResponseEntity<Map<String, Object>> getPromptForRole(@PathVariable String role) {
        try {
            Map<String, Object> result = promptService.getPromptForRole(role);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving prompt for role: " + e.getMessage()));
        }
    }

    @GetMapping("/workflows/{workflow}")
    public ResponseEntity<Map<String, Object>> getPromptForWorkflow(@PathVariable String workflow) {
        try {
            Map<String, Object> result = promptService.getPromptForWorkflow(workflow);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error retrieving prompt for workflow: " + e.getMessage()));
        }
    }

    @GetMapping("/help")
    public ResponseEntity<Map<String, Object>> getPromptHelp() {
        Map<String, Object> help = Map.of(
            "endpoints", Map.of(
                "GET /mcp/prompts", "List all available prompts",
                "GET /mcp/prompts/{name}", "Get specific prompt by name",
                "GET /mcp/prompts/categories", "List all prompt categories",
                "GET /mcp/prompts/categories/{category}", "Get prompts by category",
                "GET /mcp/prompts/roles/{role}", "Get prompt optimized for role",
                "GET /mcp/prompts/workflows/{workflow}", "Get prompt optimized for workflow"
            ),
            "available_roles", Map.of(
                "project-manager", "Gestionnaire de projet",
                "developer", "Développeur",
                "qa-tester", "Testeur QA"
            ),
            "available_workflows", Map.of(
                "sprint-planning", "Planification de sprint",
                "daily-standup", "Standup quotidien",
                "bug-triage", "Triage des bugs",
                "data-validation", "Validation des données",
                "reporting", "Génération de rapports"
            ),
            "prompt_categories", Map.of(
                "roles", "Prompts spécialisés par rôle utilisateur",
                "workflows", "Prompts pour processus métier",
                "technical", "Prompts techniques et validation",
                "system", "Prompts système et orchestration"
            ),
            "usage_examples", Map.of(
                "get_developer_prompt", "GET /mcp/prompts/roles/developer",
                "get_sprint_workflow", "GET /mcp/prompts/workflows/sprint-planning",
                "list_role_prompts", "GET /mcp/prompts/categories/roles",
                "get_system_prompt", "GET /mcp/prompts/system-orchestrator"
            )
        );
        
        return ResponseEntity.ok(help);
    }
}