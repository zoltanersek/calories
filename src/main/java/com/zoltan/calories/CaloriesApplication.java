package com.zoltan.calories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CaloriesApplication {

    //todo: style pmd cleanup, logging, error handling, e2e testing, swagger, readme
    public static void main(String[] args) {
        SpringApplication.run(CaloriesApplication.class, args);
    }

}
