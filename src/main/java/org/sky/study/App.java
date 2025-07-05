package org.sky.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EnableJpaRepositories(basePackages = "org.sky.study.repository.jpa")
@SpringBootApplication
@EnableWebSecurity
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}