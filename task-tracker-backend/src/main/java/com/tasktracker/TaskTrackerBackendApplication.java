package com.tasktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerBackendApplication.class, args);
		System.out.println("Hello World");
	}

}
