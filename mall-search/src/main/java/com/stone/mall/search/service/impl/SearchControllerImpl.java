package com.stone.mall.search.service.impl;

import com.stone.mall.search.config.MallElasticSearchConfig;
import com.stone.mall.search.constant.EsConstant;
import com.stone.mall.search.service.MallSearchService;
import com.stone.mall.search.vo.SearchParam;
import com.stone.mall.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: stone
 * @date: 2/15/22 15:32
 * @Title: SearchControllerImpl
 * @Description:
 */
@Service
public class SearchControllerImpl implements MallSearchService {

	@Autowired
	private RestHighLevelClient client;

	@Override
	public SearchResult search(SearchParam param) {
		// 动态构建出查询需要的dsl语句
		SearchRequest request = null;

		// 准备检索请求
		SearchRequest searchRequest = buildSearchRequrest();

		try {
			// 执行检索请求
			SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
			// 分析响应数据封装成我们需要的格式
			request = buildSearchResult(response);
		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}

	/**
	 * @Author: stone
	 * @Description: 准备检索请求
	 * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
	 **/
	private SearchRequest buildSearchRequrest() {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();// 构建DSL语句

		// 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）


	    // 排序，分页，高亮

		// 聚合分析

		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},sourceBuilder);
		return searchRequest;
	}



	/**
	 * @Author: stone
	 * @Description: 构建结果数据
	 **/
	private SearchRequest buildSearchResult(SearchResponse response) {

		return null;
	}


}
