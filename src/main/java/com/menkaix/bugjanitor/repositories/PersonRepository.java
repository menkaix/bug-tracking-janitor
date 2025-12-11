package com.menkaix.bugjanitor.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.menkaix.bugjanitor.models.documents.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {

    Optional<Person> findByEmail(String email);

}
