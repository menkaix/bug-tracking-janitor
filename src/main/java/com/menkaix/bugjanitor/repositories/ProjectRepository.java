package com.menkaix.bugjanitor.repositories;

import com.menkaix.bugjanitor.models.documents.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Optional<Project> findByProjectCode(String projectCode);
    Optional<Project> findByProjectName(String projectName);
}