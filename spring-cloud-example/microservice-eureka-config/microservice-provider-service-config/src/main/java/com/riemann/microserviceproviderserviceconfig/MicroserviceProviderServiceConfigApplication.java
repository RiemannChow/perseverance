package com.riemann.microserviceproviderserviceconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MicroserviceProviderServiceConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceProviderServiceConfigApplication.class, args);
	}

	@Value("${foo}")
	private String foo;

	@RequestMapping(value = "/hi")
	public String hi() {
		return foo;
	}

}
