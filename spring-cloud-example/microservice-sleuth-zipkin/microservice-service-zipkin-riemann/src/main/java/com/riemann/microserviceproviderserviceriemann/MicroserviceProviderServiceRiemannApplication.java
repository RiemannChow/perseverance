package com.riemann.microserviceproviderserviceriemann;

import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class MicroserviceProviderServiceRiemannApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceProviderServiceRiemannApplication.class, args);
    }

    private static final Logger logger = Logger.getLogger(MicroserviceProviderServiceRiemannApplication.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("/hi")
    public String callName() {
        logger.log(Level.INFO,"hi is being called");
        return "hi, I'm riemann";
    }

    @RequestMapping("/riemann")
    public String info() {
        logger.log(Level.INFO,"info is being called");
        return restTemplate.getForObject("http://localhost:8988/info",String.class);
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
