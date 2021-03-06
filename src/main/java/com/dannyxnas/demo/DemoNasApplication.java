package com.dannyxnas.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = HttpTraceAutoConfiguration.class)
public class DemoNasApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoNasApplication.class, args);
	}
}
