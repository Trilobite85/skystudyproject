package org.sky.study.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Parent class for Postgres and Redis test containers.
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public class SpringBootApplicationTest {

    private static final String POSTGRES_IMAGE = "postgres:15";
    private static final String REDIS_IMAGE = "redis:latest";
    private static final String DATABASE_NAME = "testdb";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final int REDIS_PORT = 6379;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Container
    public static PostgreSQLContainer<?> postgreContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

    @Container
    public static GenericContainer<?> redisContainer =
            new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
                    .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreContainer::getUsername);
        registry.add("spring.datasource.password", postgreContainer::getPassword);
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }

    @Test
    void testDatabaseConnection() {
        System.out.println("PostgreSQL JDBC URL: " + postgreContainer.getJdbcUrl());
    }

}