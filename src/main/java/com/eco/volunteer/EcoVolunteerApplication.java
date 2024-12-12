package com.eco.volunteer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.eco.volunteer.model")
@EnableJpaRepositories("com.eco.volunteer.repository")
public class EcoVolunteerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoVolunteerApplication.class, args);
    }
} 