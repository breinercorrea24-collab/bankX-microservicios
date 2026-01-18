package com.bca.core_banking_service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = CoreBankingServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CoreBankingServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Spring context should be created for the application");
	}

	@Test
	void mainInvokesSpringApplicationRun() {
		String[] args = new String[] {
				"--spring.main.web-application-type=none",
				"--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
				"--spring.main.lazy-initialization=true"
		};
		assertDoesNotThrow(() -> CoreBankingServiceApplication.main(args));
	}
}
