package com.ccc.gulimall.product.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.ccc.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo {
    /**
     * ????id
     */
    @TableId
    private Long attrGroupId;
    /**
     * ????
     */
    private String attrGroupName;
    /**
     * ???
     */
    private Integer sort;
    /**
     * ????
     */
    private String descript;
    /**
     * ??ͼ?
     */
    private String icon;
    /**
     * ????????id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
