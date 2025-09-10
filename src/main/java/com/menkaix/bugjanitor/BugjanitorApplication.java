package com.menkaix.bugjanitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BugjanitorApplication {

	public static void main(String[] args) {

		//SpringApplication.run(BugjanitorApplication.class, args);
		SpringApplication app = new SpringApplication(BugjanitorApplication.class);
        app.setAdditionalProfiles("local");
        app.run(args);
	}

}
