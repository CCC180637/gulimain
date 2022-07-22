package com.ccc.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 模板引擎
 * 1）、thymeleaf-starter关闭缓存
 * 2）、静态资源都放在static文件夹下就可以按照路径直接访问
 * 3）、页面放在template下，直接访问
 *      springboot，访问项目的时候，默认会找index
 *
 *
 *整合Redis
 * 1）、引入data-redis-starter
 * 2）、简单配置redis的host等信息
 * 3）、使用springboot自动配置好的springRedisTemplate来操作redis
 *
 *
 * 整合redisson做为分布式锁等框架功能
 * 1）、引入依赖
 * 2）、MyRedissonConfig给容器中配置一个RedissonClint实例即可
 *@EnableCaching   //开启缓存功能
 *
 */



@EnableFeignClients(basePackages = "com.ccc.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.ccc.gulimall.product.dao")
@SpringBootApplication
public class GulimainProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimainProductApplication.class, args);
    }

}
