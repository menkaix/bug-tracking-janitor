package com.menkaix.bugjanitor.mcptools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.menkaix.bugjanitor.models.documents.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectToolsRegistry {

    private final ProjectServiceMCPTools projectServiceTools ;

    @Autowired
    public ProjectToolsRegistry(ProjectServiceMCPTools tools){
        projectServiceTools = tools ;
    }

   
    @Tool(name = "create-project", description = "Creates a project")
    public Project createProject(Project project) {
        return projectServiceTools.createProject(project);
    }

    @Tool(name = "find-project-by-id", description = "Finds a project by its ID")
    public Optional<Project> findProjectById(String id) {
        return projectServiceTools.findProjectById(id);
    }

    @Tool(name = "update-project", description = "Updates a project")
    public Project updateProject(String id, Project projectDetails) {
        return projectServiceTools.updateProject(id, projectDetails);
    }

    @Tool(name = "delete-project", description = "Deletes a project")
    public void deleteProject(String id) {
        projectServiceTools.deleteProject(id);
    }

    @Tool(name = "find-projects", description = "Finds projects with pagination, search, and filter options")
    public Page<Project> findProjects(Pageable pageable, String search, String filter) {
        return projectServiceTools.findAllProjects(pageable, search, filter);
    }


}
