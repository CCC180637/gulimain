package com.ccc.gulimallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ccc.common.utils.R;
import com.ccc.gulimallcart.feign.ProductFeignService;
import com.ccc.gulimallcart.interceptor.CartInterceptor;
import com.ccc.gulimallcart.service.CartService;
import com.ccc.gulimallcart.vo.Cart;
import com.ccc.gulimallcart.vo.CartItem;
import com.ccc.gulimallcart.vo.SkuInfoVo;
import com.ccc.gulimallcart.vo.UserInfoTo;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ProductFeignService productFeignService;


    private final String CART_PREFIX = "gulimall:cart:";


    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String cartkey = CART_PREFIX + userInfoTo.getUserId().toString();

        List<CartItem> cartItems = getCartItems(cartkey);
        cart.setItems(cartItems);

        return cart;
    }


    //添加购物车
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();


        String res = (String) cartOps.get(skuId.toString());
        if (!StringUtils.isEmpty(res)) {
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));


            return cartItem;
        }


        CartItem cartItem = new CartItem();

        //1、远程查询当前要添加的商品信息(异步任务，解决耗时长的问题)
        CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {

            R skuInfo = productFeignService.getSkuInfo(skuId);
            SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });//商品添加到购物车
            cartItem.setCheck(true);
            cartItem.setCount(1);
            cartItem.setImage(data.getSkuDefaultImg());
            cartItem.setTitle(data.getSkuTitle());
            cartItem.setSkuId(skuId);
            cartItem.setPrice(data.getPrice());

        }, executor);

        //2.远程组合查询sku的组合信息
        CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
            List<String> saleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
            cartItem.setSkuAttr(saleAttrValues);
        }, executor);


        //全部异步任务完成
        CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
        //转换为string类型
        String jsonString = JSON.toJSONString(cartItem);
        //保存到redis
        cartOps.put(skuId.toString(), jsonString);
        return cartItem;
    }


    //获取单个购物项
    @Override
    public CartItem getCartItem(Long skuId) {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);

        return cartItem;
    }


    /**
     * 获取到我们要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }


    //获取全部的购物项
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }


    /**
     * 勾选购物项
     *
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }


    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);

    }


    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }
}
