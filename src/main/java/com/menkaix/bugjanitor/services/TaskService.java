package com.menkaix.bugjanitor.services;

import com.menkaix.bugjanitor.models.documents.Task;
import com.menkaix.bugjanitor.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TaskService(TaskRepository taskRepository, MongoTemplate mongoTemplate) {
        this.taskRepository = taskRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> findByTrackingReference(String trackingReference) {
        return taskRepository.findByTrackingReference(trackingReference);
    }

    public Task update(Task taskDetails) {
        Task task = taskRepository.findById(taskDetails.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskDetails.getId()));

        // Mettre à jour les champs
        if (taskDetails.getTitle() != null) {
            task.setTitle(taskDetails.getTitle());
        }
        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }
        if (taskDetails.getDeadLine() != null) {
            task.setDeadLine(taskDetails.getDeadLine());
        }
        if (taskDetails.getProjectCode() != null) {
            task.setProjectCode(taskDetails.getProjectCode());
        }


        return taskRepository.save(task);
    }

    public void delete(String id) {
        taskRepository.deleteById(id);
    }

    public Page<Task> findAll(Pageable pageable, String search, String filter) {
        Query query = new Query().with(pageable);
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(search)) {
            criteria.orOperator(
                    Criteria.where("title").regex(search, "i"),
                    Criteria.where("projectCode").regex(search, "i"),
                    Criteria.where("description").regex(search, "i"));
        }

        if (StringUtils.hasText(filter)) {
            // Simple filter implementation: assumes filter is in "key:value" format
            String[] filterParts = filter.split(":");
            if (filterParts.length == 2) {
                criteria.and(filterParts[0]).is(filterParts[1]);
            }
        }

        query.addCriteria(criteria);

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Task.class),
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Task.class));
    }

    public List<Task> findByProjectCode(String projectCode) {
        Query query = new Query(Criteria.where("projectCode").is(projectCode));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findByStatus(String status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findOverdueTasks() {
        Date now = new Date();
        Query query = new Query(Criteria.where("deadLine").lt(now)
                .and("doneDate").exists(false));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findUpcomingTasks() {
        Date now = new Date();
        Date sevenDaysFromNow = new Date(now.getTime() + (7 * 24 * 60 * 60 * 1000));
        Query query = new Query(Criteria.where("deadLine").gte(now).lte(sevenDaysFromNow)
                .and("doneDate").exists(false));
        return mongoTemplate.find(query, Task.class);
    }

}