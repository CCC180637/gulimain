package com.ccc.gulimallcart.vo;


import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 * 需要计算的属性必须重写他的get方法，保证每次获取属性都会进行计算
 */
public class Cart {
    List<CartItem> items;

    private Integer countNum;//商品数量

    private Integer countType; // 商品数量类型

    private BigDecimal totalAmount;//商品总价

    private BigDecimal reduce;//减免价格


    public Integer getCountNum() {
       int  count = 0;
       if (items!= null && items.size() >0){
           for (CartItem item: items) {
               count+= item.getCount();
           }
       }
        return count;
    }



    public Integer getCountType() {
        int  count = 0;
        if (items!= null && items.size() >0){
            for (CartItem item: items) {
                count+= 1;
            }
        }
        return count;
    }


    public BigDecimal getTotalAmount() {
        BigDecimal decimal = new BigDecimal("0");
        //1.计算购物项总价
        if (items!= null && items.size()>0){
            for (CartItem item: items) {
                BigDecimal totalPrice = item.getTotalPrice();
                decimal = decimal.add(totalPrice);
            }

        }
        return decimal;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
