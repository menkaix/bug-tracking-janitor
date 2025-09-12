package com.menkaix.bugjanitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ComponentScan(basePackages = {
		"com.menkaix.bugjanitor.controllers",
		"com.menkaix.bugjanitor.services",
		"com.menkaix.bugjanitor.mcptools",
		"com.menkaix.bugjanitor.configuration"
})
@EnableMongoRepositories("com.menkaix.bugjanitor.repositories")
@SpringBootApplication
public class BugjanitorApplication {

	public static void main(String[] args) {

		//SpringApplication.run(BugjanitorApplication.class, args);
		SpringApplication app = new SpringApplication(BugjanitorApplication.class);
        app.setAdditionalProfiles("local");
        app.run(args);
	}

}
