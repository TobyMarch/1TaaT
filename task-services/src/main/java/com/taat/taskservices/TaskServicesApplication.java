package com.taat.taskservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class TaskServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskServicesApplication.class, args);
	}

}
