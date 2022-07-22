package com.ccc.gulimall.product.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catelog2Vo {

    private String catalogId;  //1级父类id
    private List<Catelog3Vo> catalog3List;   //三级子分类
    private String id;
    private String name;




    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;    //父分类 ，2级分类id
        private String id;
        private String name;


    }

}
