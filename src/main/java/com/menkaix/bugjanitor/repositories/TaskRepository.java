package com.menkaix.bugjanitor.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.menkaix.bugjanitor.models.documents.Task;

@Repository
public interface TaskRepository  extends MongoRepository<Task, String> {
    
}
