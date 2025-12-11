package com.menkaix.bugjanitor.mcptools;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Person;
import com.menkaix.bugjanitor.services.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceMCPTools {

    private final PersonService personService;
    private final Gson jsonUtils;

    public PersonServiceMCPTools(PersonService personService, Gson jsonUtils) {
        this.personService = personService;
        this.jsonUtils = jsonUtils;
    }

    public Person createPerson(String personJson) {
        try {
            if (personJson == null || personJson.trim().isEmpty()) {
                throw new IllegalArgumentException("Le JSON de la personne ne peut pas être null ou vide");
            }

            Person person = jsonUtils.fromJson(personJson, Person.class);

            if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("Le prénom est requis");
            }
            if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom est requis");
            }
            if (person.getEmail() == null || person.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("L'email est requis");
            }
            if (!isValidEmail(person.getEmail())) {
                throw new IllegalArgumentException("L'email doit être valide");
            }

            return personService.create(person);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la personne: " + e.getMessage(), e);
        }
    }

    public Optional<Person> findPersonById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID de la personne ne peut pas être null ou vide");
            }
            return personService.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de la personne: " + e.getMessage(), e);
        }
    }

    public Optional<Person> findPersonByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("L'email ne peut pas être null ou vide");
            }
            return personService.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de la personne par email: " + e.getMessage(), e);
        }
    }

    public Person updatePerson(String personJson) {
        try {
            if (personJson == null || personJson.trim().isEmpty()) {
                throw new IllegalArgumentException("Le JSON de la personne ne peut pas être null ou vide");
            }

            Person personDetails = jsonUtils.fromJson(personJson, Person.class);

            if (personDetails.getId() == null || personDetails.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID de la personne est requis pour la mise à jour");
            }

            Person existingPerson = personService.findById(personDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + personDetails.getId()));

            if (personDetails.getFirstName() != null) {
                existingPerson.setFirstName(personDetails.getFirstName());
            }
            if (personDetails.getLastName() != null) {
                existingPerson.setLastName(personDetails.getLastName());
            }
            if (personDetails.getEmail() != null) {
                if (!isValidEmail(personDetails.getEmail())) {
                    throw new IllegalArgumentException("L'email doit être valide");
                }
                existingPerson.setEmail(personDetails.getEmail());
            }

            return personService.update(existingPerson);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la personne: " + e.getMessage(), e);
        }
    }

    public String deletePerson(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID de la personne ne peut pas être null ou vide");
            }

            personService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + id));

            personService.delete(id);
            return "Personne supprimée avec succès: " + id;
        } catch (Exception e) {
            return "Erreur lors de la suppression: " + e.getMessage();
        }
    }

    public Page<Person> findAllPersons(Pageable pageable, String search) {
        try {
            return personService.findAll(pageable, search);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des personnes: " + e.getMessage(), e);
        }
    }

    public List<Person> findAllPersons() {
        try {
            return personService.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des personnes: " + e.getMessage(), e);
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
