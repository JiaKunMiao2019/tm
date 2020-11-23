package com.szdq.tm.serverfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ServerFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerFinanceApplication.class, args);
    }

}
