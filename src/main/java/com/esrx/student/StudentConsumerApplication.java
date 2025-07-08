package com.esrx.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:sample.properties")
public class StudentConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentConsumerApplication.class, args);
	}

}
