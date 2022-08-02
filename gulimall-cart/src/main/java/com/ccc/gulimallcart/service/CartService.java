package com.ccc.gulimallcart.service;

import com.ccc.gulimallcart.vo.Cart;
import com.ccc.gulimallcart.vo.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {

    /**
     * 将商品添加到购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;


    /**
     * 获取购物车某个项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    Cart getCart();

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
