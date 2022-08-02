package com.ccc.gulimall.product.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties thread) {
        return new ThreadPoolExecutor(thread.getCoreSize(),        //核心线程
                thread.getMaxSize(),                          //最大线程
                thread.getKeepAliveTime(), TimeUnit.SECONDS,            //过期时间、单位
                new LinkedBlockingDeque<>(10000),    //队列数量
                Executors.defaultThreadFactory(),            //线程工厂
                new ThreadPoolExecutor.AbortPolicy());       //拒绝策略
    }

}
