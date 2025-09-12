package com.menkaix.bugjanitor.mcptools;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.menkaix.bugjanitor.models.documents.Project;
import com.menkaix.bugjanitor.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Service
public class ProjectServiceMCPTools {

    private final ProjectService projectService;

    @Autowired
    public ProjectServiceMCPTools(ProjectService projectService) {
        this.projectService = projectService;
    }

    public Project createProject(Project project) {
        return projectService.create(project);
    }

    public Optional<Project> findProjectById(String id) {
        return projectService.findById(id);
    }

    public Project updateProject(String id, Project projectDetails) {
        return projectService.update(id, projectDetails);
    }

    public void deleteProject(String id) {
        projectService.delete(id);
    }

    public Page<Project> findAllProjects(Pageable pageable, String search, String filter) {
        return projectService.findAll(pageable, search, filter);
    }
}