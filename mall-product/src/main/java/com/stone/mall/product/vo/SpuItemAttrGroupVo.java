package com.stone.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuItemAttrGroupVo {

	private String groupName;

	/**
	 * 两个属性attrName、attrValue
	 */
	private List<SpuBaseAttrVo> attrs;
}
