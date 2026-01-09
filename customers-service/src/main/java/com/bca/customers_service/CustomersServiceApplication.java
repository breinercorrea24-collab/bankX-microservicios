package com.bca.customers_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CustomersServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(CustomersServiceApplication.class);

	public static void main(String[] args) {
	    logger.info("Starting Customers Service Application");
		SpringApplication.run(CustomersServiceApplication.class, args);
		logger.info("Customers Service Application started successfully");
	}

}
