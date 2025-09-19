package com.menkaix.bugjanitor.services;

import com.menkaix.bugjanitor.models.documents.Project;
import com.menkaix.bugjanitor.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, MongoTemplate mongoTemplate) {
        this.projectRepository = projectRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> findById(String id) {
        return projectRepository.findById(id);
    }

    public Project update(Project projectDetails) {
        Project project = projectRepository.findById(projectDetails.getId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectDetails.getId()));

        // Mettre à jour les champs
        if (projectDetails.getProjectName() != null) {
            project.setProjectName(projectDetails.getProjectName());
        }
        if (projectDetails.getProjectCode() != null) {
            project.setProjectCode(projectDetails.getProjectCode());
        }
        if (projectDetails.getDescription() != null) {
            project.setDescription(projectDetails.getDescription());
        }

        return projectRepository.save(project);
    }

    public void delete(String id) {
        projectRepository.deleteById(id);
    }

    public Optional<Project> findByProjectCode(String projectCode) {
        return projectRepository.findByProjectCode(projectCode);
    }

    public Optional<Project> findByProjectName(String projectName) {
        return projectRepository.findByProjectName(projectName);
    }

    public Page<Project> findAll(Pageable pageable, String search, String filter) {
        Query query = new Query().with(pageable);
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(search)) {
            criteria.orOperator(
                    Criteria.where("projectName").regex(search, "i"),
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
                mongoTemplate.find(query, Project.class),
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Project.class));
    }

}
