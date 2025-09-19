package com.menkaix.bugjanitor.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.menkaix.bugjanitor.models.documents.Task;

@Repository
public interface TaskRepository  extends MongoRepository<Task, String> {

    Optional<Task> findByTrackingReference(String trackingReference);

}
