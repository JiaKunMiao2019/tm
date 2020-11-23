package com.szdq.tm.serverexperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ServerExperimentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerExperimentApplication.class, args);
    }

}
