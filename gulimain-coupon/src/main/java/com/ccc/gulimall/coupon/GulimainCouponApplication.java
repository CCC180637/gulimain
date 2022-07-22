package com.ccc.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、如何使用nacos作为配置中心统一管理
 * 1）、引入依赖
 *        <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *         </dependency>
 * 2）、
 */

@EnableDiscoveryClient
@SpringBootApplication
public class GulimainCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimainCouponApplication.class, args);
    }

}
