package com.menkaix.bugjanitor.services;

import com.menkaix.bugjanitor.models.documents.Person;
import com.menkaix.bugjanitor.repositories.PersonRepository;
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
public class PersonService {

    private final PersonRepository personRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PersonService(PersonRepository personRepository, MongoTemplate mongoTemplate) {
        this.personRepository = personRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Person create(Person person) {
        if (person.getEmail() == null || person.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        return personRepository.save(person);
    }

    public Optional<Person> findById(String id) {
        return personRepository.findById(id);
    }

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public Person update(Person personDetails) {
        Person person = personRepository.findById(personDetails.getId())
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + personDetails.getId()));

        // Mettre à jour les champs
        if (personDetails.getFirstName() != null) {
            person.setFirstName(personDetails.getFirstName());
        }
        if (personDetails.getLastName() != null) {
            person.setLastName(personDetails.getLastName());
        }
        if (personDetails.getEmail() != null) {
            person.setEmail(personDetails.getEmail());
        }

        // Mettre à jour automatiquement la date de modification
        person.setUpdateDate(new Date());

        return personRepository.save(person);
    }

    public void delete(String id) {
        personRepository.deleteById(id);
    }

    public Page<Person> findAll(Pageable pageable, String search) {
        Query query = new Query().with(pageable);
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(search)) {
            criteria.orOperator(
                    Criteria.where("firstName").regex(search, "i"),
                    Criteria.where("lastName").regex(search, "i"),
                    Criteria.where("email").regex(search, "i"));
        }

        query.addCriteria(criteria);

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class),
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Person.class));
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }
}
