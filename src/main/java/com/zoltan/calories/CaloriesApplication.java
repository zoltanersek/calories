package com.zoltan.calories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CaloriesApplication {

    //todo: admin endpoints, style pmd cleanup, logging, error handling, testing, e2e testing, swagger, readme
    public static void main(String[] args) {
        SpringApplication.run(CaloriesApplication.class, args);
    }

}
