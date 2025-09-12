package com.menkaix.bugjanitor.repositories;

import com.menkaix.bugjanitor.models.documents.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
}