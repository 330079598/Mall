package com.stone.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.stone.common.to.es.SkuEsModel;
import com.stone.mall.search.config.MallElasticSearchConfig;
import com.stone.mall.search.constant.EsConstant;
import com.stone.mall.search.feign.ProductFeignService;
import com.stone.mall.search.service.MallSearchService;
import com.stone.mall.search.vo.AttrResponseVo;
import com.stone.mall.search.vo.SearchParam;
import com.stone.mall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: stone
 * @date: 2/15/22 15:32
 * @Title: SearchControllerImpl
 * @Description:
 */
@Service
@Slf4j
public class SearchControllerImpl implements MallSearchService {

	@Autowired
	private RestHighLevelClient client;
	@Autowired
	private ProductFeignService productFeignService;

	/**
	 * @Author: root
	 * @Param:
	 * @return:
	 * @Description: 去es进行检索
	 **/
	@Override
	public SearchResult search(SearchParam param) {
		// 动态构建出查询需要的DSL语句
		SearchResult result = null;

		// 1. 准备检索请求
		SearchRequest searchRequest = buildSearchRequest(param);
		try {
			// 2. 执行检所请求
			SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);

			// 3. 分析响应数据,封装成我们需要的格式
			result = buildSearchResult(response, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * @Author: root
	 * @return: searchResult
	 * @Description: 准备检索请求
	 **/
	private SearchRequest buildSearchRequest(SearchParam param) {

//		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); // 构建DSL语句

//		/** 查询: 模糊匹配,过滤(按照属性,分类,品牌,价格区间,库存) */
//		// 1. 构建bool -> query
//		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//		// 1.1 must 的模糊匹配
//		if (StringUtils.isNotEmpty(param.getKeyword())) {
//			boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
//		}
//		// 1.2 bool -> filter // 按照三级分类查询
//		if (param.getCatalog3Id() != null) {
//			boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
//		}
//		// 1.2 bool -> filter // 按照品牌id进行查询
//		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
//			boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
//		}
//		// 1.2 bool -> filter 按照所有的属性进行查询
//		if (param.getAttrs() != null && param.getAttrs().size() > 0) {
//			for (String attr : param.getAttrs()) {
//				BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
//				String[] s = attr.split("_");
//				String attrId = s[0]; // 检索的属性ID
//				String[] attrValues = s[1].split(":"); // 这个属性检索用的值
//				nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
//				nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrValue", attrValues));
//				// 每一次都要生成一个nested查询
//				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
//				boolQuery.filter(nestedQuery);
//			}
//		}
//
//		// 1.2 bool -> filter 按照库存进行查询
//		boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
//		// 1.2 bool -> filter 按照价格区间进行查询
//		if (StringUtils.isNotEmpty(param.getSkuPrice())) {
//			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
//
//			String[] price = param.getSkuPrice().split("_");
//			if (price.length == 2) {
//				rangeQuery.gte(price[0]).lte(price[1]);
//			} else if (price.length == 1) {
//				if (param.getSkuPrice().startsWith("_")) {
//					rangeQuery.lte(price[0]);
//				}
//				if (param.getSkuPrice().endsWith("_")) {
//					rangeQuery.gte(price[0]);
//				}
//			}
//		}
//
//		// 将上面所有拼装好的查询条件进行封装
//		sourceBuilder.query(boolQuery);
//
//		/** 排序,分页,高亮 */
//		// 2.1 排序
//		if (StringUtils.isNotEmpty(param.getSort())) {
//			String sort = param.getSort();
//			String[] split = sort.split("_");
//			SortOrder order = split[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
//			sourceBuilder.sort(split[0], order);
//		}
//
//		// 2.2 分页
//		// form = (pageNum - 1) * pageSize
//		sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
//		sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
//
//		// 2.3 高亮
//		if (StringUtils.isNotEmpty(param.getKeyword())) {
//			HighlightBuilder highlightBuilder = new HighlightBuilder();
//			highlightBuilder.field("skuTitle");
//			highlightBuilder.preTags("<b style='color:red'>");
//			highlightBuilder.postTags("</b>");
//			sourceBuilder.highlighter(highlightBuilder);
//		}
//
//		/** 聚合分析 */
////		// 1. 品牌聚合
////		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
////		brand_agg.field("brandId").size(50);
////		// 1.1 品牌聚合的子聚合
////		// TODO:
////		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
////		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
////		sourceBuilder.aggregation(brand_agg);
////
////		// 2. 分类聚合 catalog_agg
////		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
////		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
////		sourceBuilder.aggregation(catalog_agg);
////
////		// 3. 属性聚合 attr_agg
////		NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
////		// 3.1 聚合出当前所有的attrId
////		TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
////		// 3.2 聚合分析出当前attr_id对应的名字
////		attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
////		// 3.3 聚合分析出当前attr_id对应的所有可能的属性值attrValue
////		attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
////		// 3.4 将这个子聚合加入嵌入式的聚合当中
////		attr_agg.subAggregation(attr_id_agg);
////		sourceBuilder.aggregation(attr_agg);
//
//		// TODO 1.品牌聚合
//		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
//		brand_agg.field("brandId").size(50);
//		// 品牌聚合的子聚合
//		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
//		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
//		// 将品牌聚合加入 sourceBuilder
//		sourceBuilder.aggregation(brand_agg);
//		// TODO 2.分类聚合
//		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
//		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
//		// 将分类聚合加入 sourceBuilder
//		sourceBuilder.aggregation(catalog_agg);
//		// TODO 3.属性聚合 attr_agg 构建嵌入式聚合
//		NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
//		// 3.1 聚合出当前所有的attrId
//		TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
//		// 3.1.1 聚合分析出当前attrId对应的attrName
//		attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
//		// 3.1.2 聚合分析出当前attrId对应的所有可能的属性值attrValue	这里的属性值可能会有很多 所以写50
//		attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
//		// 3.2 将这个子聚合加入嵌入式聚合
//		attr_agg.subAggregation(attrIdAgg);
//		sourceBuilder.aggregation(attr_agg);

		// 帮我们构建DSL语句的

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//构建DSL语句
		/**
		 * 模糊匹配，过滤（按照属性、分类、品牌、价格区间，库存）
		 */
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		//1.1 bool-must模糊匹配
		if (!StringUtils.isEmpty(param.getKeyword())) {
			boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
		}
		//1.2.1 bool-filter -按照三级分类ID查询
		if (param.getCatalog3Id() != null) {
			boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
		}
		//1.2.2 按照品牌ID查询
		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
			boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
		}
		//1.2.3 按照所有指定的属性进行查询
		if (param.getAttrs() != null && param.getAttrs().size() > 0) {
			//attrs=1_5寸:8寸&attrs=2_16g:8g
			for (String attr : param.getAttrs()) {
				BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
				String[] s = attr.split("_");
				String attrId = s[0];//检索的属性ID
				//检索的属性值
				String[] attrValues = s[1].split(":");
				nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
				nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
				//每一个必须都得生成一个nested查询
				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
				boolQuery.filter(nestedQuery);
			}

		}
		//1.2.4 按照是否有库存查询
		if (param.getHasStock() != null) {
			boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
		}
		//1.2.5 按照价格区间查询
		if (!StringUtils.isEmpty(param.getSkuPrice())) {
			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
			String[] s = param.getSkuPrice().split("_");
			if (s.length == 2) {
				rangeQuery.gte(s[0]).lte(s[1]);
			} else if (s.length == 1) {
				if (param.getSkuPrice().startsWith("_")) {
					rangeQuery.lte(s[0]);
				}
				if (param.getSkuPrice().endsWith("_")) {
					rangeQuery.gte(s[0]);
				}
			}

			boolQuery.filter(rangeQuery);
		}
		//把以前所有的条件封装
		sourceBuilder.query(boolQuery);
		/**
		 * 排序、分页、高亮
		 */
		//2.1 排序
		if (!StringUtils.isEmpty(param.getSort())) {
			String sort = param.getSort();
			//sort=hotScore_asc/desc
			String[] s = sort.split("_");
			SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
			sourceBuilder.sort(s[0], order);
		}
		//2.2 分页
		sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
		//2.3 高亮
		if (!StringUtils.isEmpty(param.getKeyword())) {
			HighlightBuilder builder = new HighlightBuilder();
			builder.field("skuTitle");
			builder.preTags("<b style='color:red'>");
			builder.postTags("</b>");
			sourceBuilder.highlighter(builder);
		}
		/**
		 * 聚合分析
		 */
		//品牌聚合
		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
		brand_agg.field("brandId").size(50);
		//品牌聚合的子聚合
		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
		sourceBuilder.aggregation(brand_agg);
		//分类聚合
		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
		sourceBuilder.aggregation(catalog_agg);
		//属性聚合
		NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
		//聚合出当前所有的attrId
		TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
		//聚合分析出当前attrId对应的名字
		attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
		//聚合分析出当前attrId对应所有可能的属性值attrValue
		attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue"));
		attr_agg.subAggregation(attr_id_agg);
		sourceBuilder.aggregation(attr_agg);
		String s = sourceBuilder.toString();
		System.out.println("构建的dsl语句：" + s);
		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
		return searchRequest;
	}


	/**
	 * @Author: root
	 * @Description: 构建结果数据
	 **/
	private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
//		SearchResult result = new SearchResult();
//		// 1. 返回的所有查询到的商品
//		SearchHits hits = response.getHits();
//
//		List<SkuEsModel> esModels = new ArrayList<>();
//		if (hits.getHits() != null && hits.getHits().length > 0) {
//			for (SearchHit hit : hits.getHits()) {
//				String sourceAsString = hit.getSourceAsString();
//				// ES中检索得到的对象
//				SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
//				if (!StringUtils.isEmpty(Param.getKeyword())) {
//					// 1.1 获取标题的高亮属性
//					HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
//					String highlightFields = skuTitle.getFragments()[0].string();
//					// 1.2 设置文本高亮
//					esModel.setSkuTitle(highlightFields);
//				}
//				esModels.add(esModel);
//			}
//		}
//		result.setProducts(esModels);
//
//		// 2. 当前所有商品涉及到的所有属性信息
//		ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
//		ParsedNested attr_agg = response.getAggregations().get("attr_agg");
//		ParsedLongTerms attr_id = attr_agg.getAggregations().get("attr_id_agg");
//		for (Terms.Bucket bucket : attr_id.getBuckets()) {
//			SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
//			// 2.1 得到属性的id
//			attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
//			// 2.2 得到属性的名字
//			String attr_name = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
//			attrVo.setAttrName(attr_name);
//			// 2.3 得到属性的所有值
//			List<String> attr_value = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
//			attrVo.setAttrValue(attr_value);
//			attrVos.add(attrVo);
//		}
//		result.setAttrs(attrVos);
//
//		// 3. 当前所有商品涉及到的所有品牌信息
//		ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
//		ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
//		for (Terms.Bucket bucket : brand_agg.getBuckets()) {
//			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
//			// 3.1 得到品牌的id
//			long brnadId = bucket.getKeyAsNumber().longValue();
//			brandVo.setBrandId(brnadId);
//			// 3.2 得到品牌的名称
//			String brand_name = ((ParsedStringTerms) (bucket.getAggregations().get("brand_name_agg"))).getBuckets().get(1).getKeyAsString();
//			brandVo.setBrandName(brand_name);
//
//			// 3.3 得到品牌的图片
//			String brand_img = ((ParsedStringTerms) (bucket.getAggregations().get("brand_img_agg"))).getBuckets().get(0).getKeyAsString();
//			brandVo.setBrandImg(brand_img);
//			brandVos.add(brandVo);
//		}
//		result.setBrands(brandVos);
//
//
//		// 4. 当前所有商品涉及到的所有分类信息
//		ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
//		List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
//		List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
//		for (Terms.Bucket bucket : buckets) {
//			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
//			// 得到分类id
//			String keyAsStrig = bucket.getKeyAsString();
//			catalogVo.setCatalogId(Long.parseLong(keyAsStrig));
//			// 得到分类名
//			ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
//			String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
//			catalogVo.setCatalogName(catalog_name);
//			catalogVos.add(catalogVo);
//		}
//
//		// 5.1 分页信息 -- 页码
//		result.setPageNum(Param.getPageNum());
//
//
//		// 5.2 分页信息 -- 总记录数
//		long total = hits.getTotalHits().value;
//		result.setTotal(total);
//
//		// 5.3 分页信息 -- 总页码
//		int totalPages = (int) (total / EsConstant.PRODUCT_PAGESIZE + 0.999999999999);
//		result.setTotalPages(totalPages);
//		return result;
		SearchResult result = new SearchResult();
		//1、返回所有查询到的商品
		SearchHits hits = response.getHits();
		List<SkuEsModel> list = new ArrayList<>();
		if (hits.getHits() != null && hits.getHits().length > 0) {
			for (SearchHit hit : hits.getHits()) {
				String sourceAsString = hit.getSourceAsString();
				SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
				//高亮标题处理
				if (!StringUtils.isEmpty(param.getKeyword())) {
					HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
					String string = skuTitle.getFragments()[0].string();
					esModel.setSkuTitle(string);
				}
				list.add(esModel);
			}
		}
		result.setProducts(list);
		//2、当前所有商品涉及到的所有属性信息
		List<SearchResult.AttrVo> attrVos = new ArrayList<>();
		ParsedNested attr_agg = response.getAggregations().get("attr_agg");
		ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
		for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
			//得到属性ID
			SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
			long attrId = bucket.getKeyAsNumber().longValue();
			attrVo.setAttrId(attrId);
			//得到属性名
			String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
			attrVo.setAttrName(attrName);
			//得到属性的所有值
			List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
				String keyAsString = ((Terms.Bucket) item).getKeyAsString();
				return keyAsString;
			}).collect(Collectors.toList());
			attrVo.setAttrValue(attrValues);

			attrVos.add(attrVo);
		}
		result.setAttrs(attrVos);
		//3、当前所有商品涉及到的所有品牌信息
		List<SearchResult.BrandVo> brands = new ArrayList<>();
		ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
		List<? extends Terms.Bucket> brandAggBuckets = brand_agg.getBuckets();
		for (Terms.Bucket bucket : brandAggBuckets) {
			//获取品牌信息
			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
			//获取品牌的ID
			long brandId = bucket.getKeyAsNumber().longValue();
			brandVo.setBrandId(brandId);
			//获取品牌名字
			ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
			String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
			brandVo.setBrandName(brandName);
			//获取品牌图片
			ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
			String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
			brandVo.setBrandImg(brandImg);
			brands.add(brandVo);
		}
		result.setBrands(brands);
		//4、当前所有商品涉及到的所有分类信息
		List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
		ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
		List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
		for (Terms.Bucket bucket : buckets) {
			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
			//获取品牌ID
			String keyAsString = bucket.getKeyAsString();
			catalogVo.setCatalogId(Long.parseLong(keyAsString));
			//得到品牌名字
			ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
			String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
			catalogVo.setCatalogName(catalog_name);
			catalogVos.add(catalogVo);
		}
		result.setCatalogs(catalogVos);
		//5、分页信息-页码
		result.setPageNum(param.getPageNum());
		//5、分页信息-总记录数
		long total = hits.getTotalHits().value;
		result.setTotal(total);
		//5、分页信息-总页码
		int totalPages = total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : (int) total / EsConstant.PRODUCT_PAGESIZE + 1;
		result.setTotalPages(totalPages);
		//可遍历的页码
		List<Integer> pageNavs = new ArrayList<>();
		for (int i = 1; i <= totalPages; i++) {
			pageNavs.add(i);
		}
		result.setPageNavs(pageNavs);


		return result;
	}


}
