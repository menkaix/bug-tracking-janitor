package com.menkaix.bugjanitor.mcptools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.bugjanitor.models.documents.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PersonToolsRegistry {

    private final PersonServiceMCPTools personServiceTools;
    private final Gson jsonUtils;

    public PersonToolsRegistry(PersonServiceMCPTools tools, Gson jsonUtils) {
        personServiceTools = tools;
        this.jsonUtils = jsonUtils;
    }

    @Tool(name = "create-person", description = "Creates a new person from JSON input. Required fields: firstName (string), lastName (string), email (string, valid email format). Returns the created person with auto-generated ID and timestamps.")
    public String createPerson(String personJson) {
        try {
            Person person = personServiceTools.createPerson(personJson);
            return jsonUtils.toJson(person);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-person-by-id", description = "Retrieves a specific person by their unique identifier. Parameter: id (string, required). Returns the complete person object if found, or an error message if the person doesn't exist.")
    public String findPersonById(String id) {
        try {
            Optional<Person> person = personServiceTools.findPersonById(id);
            if (person.isPresent()) {
                return jsonUtils.toJson(person.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Personne non trouvée avec l'ID: " + id);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-person-by-email", description = "Retrieves a specific person by their email address. Parameter: email (string, required). Returns the complete person object if found, or an error message if no person with that email exists.")
    public String findPersonByEmail(String email) {
        try {
            Optional<Person> person = personServiceTools.findPersonByEmail(email);
            if (person.isPresent()) {
                return jsonUtils.toJson(person.get());
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", "Personne non trouvée avec l'email: " + email);
                return jsonUtils.toJson(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "update-person", description = "Updates an existing person with new data from JSON input. Required field: id (string, person identifier). Updatable fields: firstName (string), lastName (string), email (string, valid email format). Returns the updated person with refreshed updateDate timestamp.")
    public String updatePerson(String personDetails) {
        try {
            Person person = personServiceTools.updatePerson(personDetails);
            return jsonUtils.toJson(person);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "delete-person", description = "Permanently removes a person from the system. Parameter: id (string, required, person identifier). Returns a confirmation message upon successful deletion or an error if the person doesn't exist.")
    public String deletePerson(String id) {
        try {
            String result = personServiceTools.deletePerson(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return jsonUtils.toJson(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "find-persons", description = "Retrieves persons with pagination and search capabilities. Parameters: page (int, default 0, zero-based page number), size (int, default 10, max 100, number of items per page), search (string, optional, searches in firstName, lastName, and email). Returns paginated results with person list, total count, page info, and navigation flags.")
    public String findPersons(int page, int size, String search) {
        try {
            if (page < 0)
                page = 0;
            if (size <= 0)
                size = 10;
            if (size > 100)
                size = 100;

            Pageable pageable = PageRequest.of(page, size);
            Page<Person> persons = personServiceTools.findAllPersons(pageable, search);

            Map<String, Object> result = new HashMap<>();
            result.put("content", persons.getContent());
            result.put("totalElements", persons.getTotalElements());
            result.put("totalPages", persons.getTotalPages());
            result.put("currentPage", persons.getNumber());
            result.put("size", persons.getSize());
            result.put("hasNext", persons.hasNext());
            result.put("hasPrevious", persons.hasPrevious());

            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }

    @Tool(name = "list-all-persons", description = "Retrieves all persons in the system without pagination. Returns a complete list of all persons. Use with caution on large datasets.")
    public String listAllPersons() {
        try {
            List<Person> persons = personServiceTools.findAllPersons();
            Map<String, Object> result = new HashMap<>();
            result.put("persons", persons);
            result.put("count", persons.size());
            result.put("message", "Toutes les personnes trouvées");
            return jsonUtils.toJson(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return jsonUtils.toJson(error);
        }
    }
}
