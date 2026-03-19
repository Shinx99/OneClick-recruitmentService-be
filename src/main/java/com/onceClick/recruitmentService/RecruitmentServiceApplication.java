package com.onceClick.recruitmentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RecruitmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecruitmentServiceApplication.class, args);
	}

}
