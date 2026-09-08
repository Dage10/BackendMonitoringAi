package com.david.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class MonitoringApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoringApiApplication.class, args);
	}

}
