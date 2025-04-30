package com.tasktracker;

import com.tasktracker.repository.ProjectRepository;
import com.tasktracker.repository.TaskRepository;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.util.DataPrinter;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class TaskTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerBackendApplication.class, args);
		System.out.println("Hello World");
	}

	@Bean
	CommandLineRunner run(DataPrinter printer) {
		return args -> printer.printData();
	}
}



