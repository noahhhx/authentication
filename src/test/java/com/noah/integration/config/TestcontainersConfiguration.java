package com.noah.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
  
//  @Bean
//  @ServiceConnection
//  PostgreSQLContainer<?> postgresContainer() {
//    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
//        .withInitScript("./sql/postgres/POSTGRES.sql");
//  }

  @Bean
  @ServiceConnection
  MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
  }
  
}
