package com.example.telegram_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramDbConnectionProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramDbConnectionProjectApplication.class, args);
	}

}
