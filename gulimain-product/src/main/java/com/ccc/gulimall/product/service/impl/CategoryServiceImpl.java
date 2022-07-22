package com.ccc.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ccc.gulimall.product.service.CategoryBrandRelationService;
import com.ccc.gulimall.product.vo.Catelog2Vo;
import org.aspectj.lang.annotation.Around;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;

import com.ccc.gulimall.product.dao.CategoryDao;
import com.ccc.gulimall.product.entity.CategoryEntity;
import com.ccc.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redisson;

    private Map<String, Object> cache = new HashMap<>();

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查询出所有分类
        final List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //组装成父子的树形结构

        //2.1、找到所有的一级分类
        List<CategoryEntity> leveEntity = categoryEntities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildrens(menu, categoryEntities));
                    return menu;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());

        return leveEntity;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODD  1、检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findCatelogPath(catelogId, paths);

        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[0]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */

    // @CacheEvict(value = "catalog" ,key = "'getLevel1Categroys'")   失效模式
    @Caching(evict = {
            @CacheEvict(value = "category", key = "'getLevel1Categroys'"),
            @CacheEvict(value = "category", key = "'getCatalogJson'")
    })
    @Transactional  //事务
    @Override
    public void updateDetail(CategoryEntity category) {
        categoryBrandRelationService.updateDetail(category.getCatId(), category.getName());
        this.updateById(category);

    }

    private List<Long> findCatelogPath(Long catelogId, List<Long> paths) {
        //1.收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findCatelogPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> childern = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return childern;
    }


    /**
     * 1、 每一个需要缓存的数据我们都来指定要放到那个名字的缓存【缓存的分区{按照业务类型分}】
     * 2、@Cacheable{"ccc"}
     * 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用
     * 如果缓存中没有，会调用方法，然后将结果存入缓存
     * 3、默认行为
     * 1）、如果缓存中有，方法不用调用
     * 2）、key默认自动生成，缓存的名字：：SimpleKey{}（自主生成key值）
     * 3）、缓存的value值，默认使用jdk序列化机制，将序列化后的数据放到redis
     * 4）、默认ttl时间 -1s
     * <p>
     * <p>
     * 自定义
     * 1）、指定生成的缓存使用的key ：key属性指定 ，接受一个SpEL
     * 2）、指定缓存数据的存活时间
     * 3）、将数据保存为json格式
     *        自定义RedisCacheConfiguration即可
     *
     *  4、spring-Cache的不足
     *  1）、读模式：
     *          缓存穿透：查询一个null数据 。解决缓存空数据： Cache-null-values=true
     *          缓存击穿：大量并发进来同时查询一个正好过期的数据。解决:加锁；？默认是无锁的：sync=true
     *          缓存雪崩：大量的key同时过期。解决：加上过期时间。加随机时间
     *  2）、写模式：（缓存与数据库一致）
     *          1）、读写加锁
     *          2）、引入Canal，感知mysql的更新去更新数据库
     *          3）、读多写少，直接去数据库查询就行
     *
     *  总结：
     *      常规数据（读多写少，即时性，一致性要求不高的数据）；完全可以使用Spring-Cache；写模式（只要缓存的数据有过期时间就可以）
     *      特殊数据：特殊设计
     *
     * @return
     */

    @Cacheable(value = "category", key = "#root.method.name",sync = true)  //代表当前方法的结果需要缓存，如果缓存中有，方法不调用，如果没有会调用方法，最后将结果放入缓存中
    @Override
    public List<CategoryEntity> getLevel1Categroys() {
        System.out.println("getLevel1Categorys.....");
        Long millis = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
        return categoryEntities;
    }


    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1、查出所有一级分类
        List<CategoryEntity> levelCategorys = getParent_id(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> print_cid = levelCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_id(selectList, v.getCatId());
            //2.封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());


                    //找到当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3 = getParent_id(selectList, l2.getCatId());
                    if (level3 != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());

            }
            return catelog2Vos;
        }));

        return print_cid;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型；【序列化与反序列化】

        /**
         * 1、空结果缓存：解决缓存穿透
         * 2、设置过期时间（加随机值）：解决缓存雪崩
         * 3、加锁，解决缓存击穿
         */

        //加入缓存逻辑
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        //判断缓存中是否有数据
        if (StringUtils.isEmpty(catalogJson)) {
            //没有该数据的话就调用查询的方法进行查询
            System.out.println("缓存不命中，需要查询数据库。。。。。");
            Map<String, List<Catelog2Vo>> getCatalogJsonForDb = getCatalogJsonForDbRedisLock();
            return getCatalogJsonForDb;
        }
        System.out.println("缓存命中，直接返回。。。");
        Map<String, List<Catelog2Vo>> parseObject = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return parseObject;
    }


    public Map<String, List<Catelog2Vo>> getCatalogJsonForDbRedissonLock() {

        //1、占分布式锁，去redis占坑
        //Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "111");
        //在占锁的同时，给锁设置过期时间避免服务宕机照成死锁

        RLock lock = redisson.getLock("catalogJson");
        lock.lock();

        //加锁成功...执行业务
        //设置过期时间，必须和加锁是同步的，原子的
        Map<String, List<Catelog2Vo>> dataFromDb = getDataFromDb();
        try {

            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();

        }

        //获取值对比+对比成功删除=原子操作  lua脚本解锁
        //stringRedisTemplate.delete("lock");  //删除锁\
        return dataFromDb;

    }


    public Map<String, List<Catelog2Vo>> getCatalogJsonForDbRedisLock() {

        //1、占分布式锁，去redis占坑
        //Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "111");
        //在占锁的同时，给锁设置过期时间避免服务宕机照成死锁

        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 200, TimeUnit.SECONDS);


        if (lock) {
            System.out.println("获取分布式锁成功.....");
            //加锁成功...执行业务
            //设置过期时间，必须和加锁是同步的，原子的
            Map<String, List<Catelog2Vo>> dataFromDb = getDataFromDb();
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //删除锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                stringRedisTemplate.execute(new DefaultRedisScript<Integer>(script, Integer.class), Arrays.asList("lock"), uuid);

            }

            //获取值对比+对比成功删除=原子操作  lua脚本解锁
            //stringRedisTemplate.delete("lock");  //删除锁
            return dataFromDb;
        } else {
            //加锁失败。。。重试。synchronized()
            System.out.println("获取分布式锁失败，重试。。。");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonForDbRedisLock();  //自旋的方式，没有加锁成功就调用方法重新执行
        }


    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {

            //没有该数据的话就调用查询的方法进行查询
            Map<String, List<Catelog2Vo>> parseObject = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return parseObject;

        }
        System.out.println("查询了数据库。");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1、查出所有一级分类
        List<CategoryEntity> levelCategorys = getParent_id(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> print_cid = levelCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_id(selectList, v.getCatId());
            //2.封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());


                    //找到当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3 = getParent_id(selectList, l2.getCatId());
                    if (level3 != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());

            }
            return catelog2Vos;
        }));
        //将查询到的时候放入缓存，将对象转换为json放在缓存
        String jsonString = JSON.toJSONString(print_cid);
        stringRedisTemplate.opsForValue().set("catalogJson", jsonString, 1, TimeUnit.DAYS);
        return print_cid;
    }


    //TODO  产生堆外内存溢出：OutOfDirectMemoryError

    //从数据库查询并封装数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonForDb() {
/*
        Map<String,List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
        if (cache.get("catalogJson") == null){
            return catalogJson;

        }  */

        //只要是同一把锁，就能锁住需要这个锁的全部线程
        synchronized (this) {
            //判断缓存中是否有数据
            return getDataFromDb();
        }


    }


    private List<CategoryEntity> getParent_id(List<CategoryEntity> selectList, Long parent_cid) {
        //  return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> parent_cid.equals(item.getParentCid())).collect(Collectors.toList());
        return collect;
    }
}