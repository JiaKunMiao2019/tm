package com.szdq.tm.serverprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ServerPrintApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerPrintApplication.class, args);
    }

}
