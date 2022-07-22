package com.ccc.gulimall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class GulimainOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimainOrderApplication.class, args);
    }

}
