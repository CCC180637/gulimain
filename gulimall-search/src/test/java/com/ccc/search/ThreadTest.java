package com.ccc.search;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main....start....");
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).whenComplete((res, excption) -> {
            //虽然能感知到异常信息，但是没有办法修改返回的数据
            System.out.println("异步任务执行成功了。。。结果是：" + res + ";异常是：" + excption);
        }).exceptionally(throwable -> {
            return 10;
        });
       Integer integer= future.get();
        System.out.println("main....end...."+integer);
    }
}
