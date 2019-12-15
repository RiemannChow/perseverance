package com.riemann.microserviceproviderservicehi;

import java.util.logging.Level;
import java.util.logging.Logger;
import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class MicroserviceProviderServiceHiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceProviderServiceHiApplication.class, args);
	}

	private static final Logger logger = Logger.getLogger(MicroserviceProviderServiceHiApplication.class.getName());

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/hi")
	public String callName() {
		logger.log(Level.INFO,"calling trace microservice-service-zipkin-hi");
		return restTemplate.getForObject("http://localhost:8989/riemann", String.class);
	}

	@RequestMapping("/info")
	public String info() {
		logger.log(Level.INFO,"calling trace microservice-service-zipkin-hi");
		return "I'm microservice-service-zipkin-hi";
	}

	@Bean
	public Sampler defaultSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}
}
