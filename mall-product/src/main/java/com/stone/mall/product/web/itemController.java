package com.stone.mall.product.web;

import com.stone.mall.product.service.SkuInfoService;
import com.stone.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class itemController {

	@Autowired
	SkuInfoService skuInfoService;

	/**
	 * @Param: skuId
	 * @Description: 展示当前sku的详情
	 **/
	@GetMapping("/{skuId}.html")
	public String skuItem(@PathVariable("skuId") String skuId) {
		System.out.println("查询" + skuId + "详情");
		Long skuid = Long.parseLong(skuId);
		SkuItemVo skuItemVo = skuInfoService.item(skuid);
		return "item";
	}
}
