package com.stone.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: stone
 * @date: 2/15/22 15:30
 * @Title: SearchParam
 * @Description: 封装页面所有可能传递过来的参数（查询条件）
 */

@Data
public class SearchParam {

	private String keyword; // 页面传递过来的参数
	private Long catalog3Id; // 三级分类id
	private String sort; // 排序条件
	private Integer hasStock; // 是否有货
	private String skuPrice; // 价格区间查询
	private List<Long> brandId; // 安装品牌进行查询，可以进行多选
	private List<String> attrs; // 按照属性进行筛选
	private Integer pageNum; // 页码
}
