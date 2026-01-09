package com.bca.bootcoin_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootcoinServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(BootcoinServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Bootcoin Service application");
		SpringApplication.run(BootcoinServiceApplication.class, args);
	}

}
