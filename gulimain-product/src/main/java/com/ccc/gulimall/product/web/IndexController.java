package com.ccc.gulimall.product.web;


import com.ccc.gulimall.product.entity.CategoryEntity;
import com.ccc.gulimall.product.service.CategoryService;
import com.ccc.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;


    @Autowired
    RedissonClient redisson;


    @Autowired
    RedisTemplate redisTemplate;


    @GetMapping({"/", "/index"})
    public String indexPage(Model model) {
        //TODO 查出所有的一级分类

        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categroys();

        //视图解析器拼串；
        //classpath://template/  +返回值 + .html
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }


    //index/catalog.json
    @ResponseBody    //需要给前台页面返回json数据
    @RequestMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;
    }


    @ResponseBody
    @GetMapping("/hello")
    public String hello() {

        //1、获取一把锁，只要锁名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");
        //2、加锁
        lock.lock();   //阻塞式的等待，默认加的锁都是30s时间
        //1）、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s，不用担心业务时间上，过期自动删除
        //2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除
        try {
            System.out.println("加锁成功，执行业务" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            //解锁
            lock.unlock();
            System.out.println("释放锁" + Thread.currentThread().getId());
        }

        return "hello";
    }


    /**
     * 保证一定能读取到最新的数据，修改期间，写锁是一个排它锁（互斥锁）。读锁是一个贡献锁
     * 写锁没释放读锁就必须等待
     * 读+读 ：相当于无锁，并发读，只会在redis中记录好，所有当前的读锁，他们都会同时加锁成功
     * 写+写 ：阻塞方式
     * 写+读 ：等待写锁释放
     * 读+写 ：有读锁，写锁也需要等待
     * 只要写的存在，都需要等待
     * @return
     */
    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.writeLock();
        try {
            //改数据加锁，读数据加锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(3000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //删除锁
            rLock.unlock();
        }
        return s;
    }


    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");

        String s = "";
        RLock rLock = lock.readLock();
        try {
            System.out.println("读加锁成功！" + Thread.currentThread().getId());
            s = (String) redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放");
        }
        return s;
    }


    /**
     * 车库停车
     * 3号位
     * 信号量也可以用作分布式限流
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        //park.acquire();   //获取一个信号，获取一个值，占一个车位
        boolean b = park.tryAcquire();   //有库存就停，没库存就溜

        return "ok"+b;

    }


    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();  // 释放一个车位
        return "ok";

    }



    @GetMapping("/lockDoor")
    @ResponseBody
    public String gogogo() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();

        return "锁门了...";
    }


    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogo(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();
        return "这个同学回家了";
    }


}
