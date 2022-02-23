package com.stone.mall.search.controller;

import com.stone.mall.search.service.MallSearchService;
import com.stone.mall.search.vo.SearchParam;
import com.stone.mall.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: stone
 * @date: 2/15/22 15:12
 * @Title: SearchController
 * @Description:
 */
@Controller
public class SearchController {

	@Autowired
	MallSearchService mallSearchService;

	@GetMapping("/list.html")
	public String listPage(SearchParam param, Model model) {
		// 根据传递来的页面的查询参数，去es中检索商品
		SearchRequest result = mallSearchService.search(param);
		model.addAttribute("result", result);
		return "list";
	}
}
