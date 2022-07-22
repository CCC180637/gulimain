package com.ccc.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import javafx.scene.image.PixelFormat;
import lombok.Data;

/**
 * spu??Ϣ???
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ??Ʒid
	 */
	@TableId(type = IdType.INPUT)
	private Long spuId;
	/**
	 * ??Ʒ???
	 */
	private String decript;

}
