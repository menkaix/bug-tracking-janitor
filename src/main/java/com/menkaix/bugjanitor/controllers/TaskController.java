package com.menkaix.bugjanitor.controllers;

import com.menkaix.bugjanitor.services.TaskService;
import com.menkaix.bugjanitor.models.documents.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/task")
@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.create(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskService.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task taskDetails) {
        taskDetails.setId(id);
        try {
            Task updatedTask = taskService.update(taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<Task> getAllTasks(Pageable pageable,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) String filter) {
        return taskService.findAll(pageable, search, filter);
    }
}
