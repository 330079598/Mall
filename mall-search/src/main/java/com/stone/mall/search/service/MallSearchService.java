package com.stone.mall.search.service;

import com.stone.mall.search.vo.SearchParam;
import org.elasticsearch.action.search.SearchRequest;

/**
 * @author: stone
 * @date: 2/15/22 15:32
 * @Title: SearchController
 * @Description:
 */
public interface MallSearchService {
	/**
	 * @Author: stone
	 * @Param: 检索的所有参数
	 * @return: 返回检索的而结果
	 * @Description:
	 **/
	SearchRequest search(SearchParam param);
}
