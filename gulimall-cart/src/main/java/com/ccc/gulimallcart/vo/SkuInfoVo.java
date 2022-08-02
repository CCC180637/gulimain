package com.ccc.gulimallcart.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuInfoVo {
    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * sku???
     */
    private String skuName;
    /**
     * sku????????
     */
    private String skuDesc;
    /**
     * ????????id
     */
    private Long catalogId;
    /**
     * Ʒ??id
     */
    private Long brandId;
    /**
     * Ĭ??ͼƬ
     */
    private String skuDefaultImg;
    /**
     * ???
     */
    private String skuTitle;
    /**
     * ?????
     */
    private String skuSubtitle;
    /**
     * ?۸
     */
    private BigDecimal price;
    /**
     * ????
     */
    private Long saleCount;


}
