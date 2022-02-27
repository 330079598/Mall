package com.stone.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ItemSaleAttrVo {
	private Long attrId;

	private String attrName;

	/**
	 * AttrValueWithSkuIdVo两个属性 attrValue、skuIds
	 */
	private List<AttrValueWithSkuIdVo> attrValues;

}
