package com.stone.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.stone.common.to.es.SkuEsModel;
import com.stone.mall.search.config.MallElasticSearchConfig;
import com.stone.mall.search.constant.EsConstant;
import com.stone.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: stone
 * @Title: ProductSaveServiceImpl
 * @date: 2022/2/7 11:56
 * @Description:
 */

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {
		// 保存到es中

		// 1. 给es中建立索引,product,建立好映射关系

		// 2. 给es中保存这些数据
		BulkRequest bulkRequest = new BulkRequest();
		for (SkuEsModel model : skuEsModelList) {
			// 构造器中保存请求
			IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
			indexRequest.id(model.getSkuId().toString());
			String s = JSON.toJSONString(model);
			indexRequest.source(s, XContentType.JSON);

			bulkRequest.add(indexRequest);
		}
		BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);

		// 如果批量出现错误
		boolean b = bulk.hasFailures();
		if (b) {
			List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
				return item.getId();
			}).collect(Collectors.toList());
			log.error("商品上架错误:{}", collect);
		}
		List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
			return item.getId();
		}).collect(Collectors.toList());
		log.info("商品上架成功:{}", collect);
		return b;
	}
}
