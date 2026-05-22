package com.example.practical_class_es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PracticalClassEsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PracticalClassEsApplication.class, args);
	}

}
