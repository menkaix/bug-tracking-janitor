package com.menkaix.bugjanitor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.menkaix.bugjanitor.mcp.resources.MCPResourceProvider;

import java.util.Map;

@RestController
@RequestMapping("/mcp/resources")
public class MCPResourceController {

    private final MCPResourceProvider resourceProvider;

    @Autowired
    public MCPResourceController(MCPResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @GetMapping("/**")
    public ResponseEntity<Map<String, Object>> getResource(
            @RequestParam(value = "uri", required = true) String uri) {
        
        try {
            Map<String, Object> result = resourceProvider.getResource(uri);
            
            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listAvailableResources() {
        Map<String, Object> resources = Map.of(
            "project_resources", Map.of(
                "projects", "List all projects",
                "projects/{id}", "Get specific project by ID",
                "projects/{projectCode}/tasks", "Get tasks for a specific project"
            ),
            "task_resources", Map.of(
                "tasks", "List all tasks",
                "tasks/{id}", "Get specific task by ID",
                "tasks/by-tracking-ref/{trackingRef}", "Get task by tracking reference",
                "tasks/by-status/{status}", "Get tasks by status",
                "tasks/overdue", "Get overdue tasks",
                "tasks/upcoming", "Get upcoming tasks (next 7 days)"
            ),
            "schema_resources", Map.of(
                "schemas/project", "Project JSON schema",
                "schemas/task", "Task JSON schema"
            ),
            "server_resources", Map.of(
                "server/health", "Server health status",
                "server/info", "Server information and capabilities"
            ),
            "metrics_resources", Map.of(
                "metrics/projects/count", "Total project count",
                "metrics/tasks/count", "Total task count"
            )
        );
        
        return ResponseEntity.ok(resources);
    }
}