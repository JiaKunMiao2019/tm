package com.szdq.tm.serverproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//商家微服务
@SpringBootApplication
@EnableEurekaClient
public class ServerProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerProductApplication.class, args);
    }

}
