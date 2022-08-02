package com.ccc.gulimallcart.controller;


import com.ccc.common.constant.AuthServerConstant;
import com.ccc.gulimallcart.service.CartService;
import com.ccc.gulimallcart.vo.Cart;
import com.ccc.gulimallcart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;


    @GetMapping("/cartList.html")
    public String cartListPage(HttpSession session, Model model) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        //判断是否登录
        if (!StringUtils.isEmpty(attribute)) {
            Cart cart = cartService.getCart();
            model.addAttribute("cart", cart);
            return "cartList";
        }

        return "redirect:http://auth.gulimall.com/login.html";
    }


    /**
     * 添加商品到购物车
     * <p>
     * RedirectAttributes ra
     * ra.addFlashAttribute()：将数据放在session里面可以在页面取出，但是只能取一次
     * ra.addAttribute("skuId",skuId);将数据放在url后面
     *
     * @param skuId
     * @param num
     * @param ra
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes ra) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addToCart(skuId, num);

        //请求携带参数

        ra.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面，再次查询购物车数据即可
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);

        return "success";
    }


    //修改购物项
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check) {
        cartService.checkItem(skuId, check);

        return "redirect:http://cart.gulimall.com/cartList.html";
    }

    //修改商品数量
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num) {

        cartService.countItem(skuId, num);
        return "redirect:http//cart.gulimall.com/cartList.html";
    }


    //删除购物车商品
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cartList.html";
    }

}
