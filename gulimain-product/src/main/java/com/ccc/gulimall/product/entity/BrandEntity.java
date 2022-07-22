package com.ccc.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.ccc.common.group.AddGroup;
import com.ccc.common.group.ListValue;
import com.ccc.common.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * Ʒ?
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Ʒ??id
	 */
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	@NotNull(message = "修改必须指定id",groups = {UpdateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * Ʒ????
	 */
	@NotBlank(message = "品牌名必须提交" ,groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * Ʒ??logo??ַ
	 */

	@NotBlank(groups = {AddGroup.class})
	@URL(message = "logo必须是一个合法的url地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * ???
	 */
	private String descript;
	/**
	 * ??ʾ״̬[0-????ʾ??1-??ʾ]
	 */

	@ListValue(vals={0,1},groups = {AddGroup.class})
	private Integer showStatus;
	/**
	 * ????????ĸ
	 */


	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索字母必须是一个字母",groups = {AddGroup.class,UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */

	@NotEmpty
	@Min(value = 0 ,message = "排序必须大于等于0")
	private Integer sort;

}
