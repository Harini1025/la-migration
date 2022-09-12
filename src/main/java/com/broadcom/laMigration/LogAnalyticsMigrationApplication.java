package com.broadcom.laMigration;

import com.broadcom.laMigration.service.LAMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LogAnalyticsMigrationApplication implements ApplicationRunner {

	@Autowired
	private LAMigrationService service;

	public static void main(String[] args) {
		SpringApplication.run(LogAnalyticsMigrationApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.migrateLog();
	}
}
