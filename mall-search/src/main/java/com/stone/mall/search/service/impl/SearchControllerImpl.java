package com.stone.mall.search.service.impl;

import com.stone.mall.search.feign.ProductFeignService;
import com.stone.mall.search.service.MallSearchService;
import com.stone.mall.search.vo.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public SearchRequest search(SearchParam param) {
//		// 动态构建出查询需要的dsl语句
//		SearchRequest result = null;
//
//		// 准备检索请求
//		SearchRequest searchRequest = buildSearchRequrest(param);
//
//		try {
//			// 执行检索请求
//			SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
//			// 分析响应数据封装成我们需要的格式
//			result = buildSearchResult(response, param);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//
//		return result;
//	}
//
//	/**
//	 * @Author: stone
//	 * @Description: 准备检索请求
//	 * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
//	 **/
//	private SearchRequest buildSearchRequrest(SearchParam param) {
//		// 构建DSL语句
//		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();// 构建DSL语句F
//
//		// 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
//		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//		// 1.must模糊匹配
//		if (StringUtils.isNotEmpty(param.getKeyword())) {
//			boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
//		}
//		// 1.2 bool - filter Catalog3Id
//		if (StringUtils.isNotEmpty(String.valueOf(param.getCatalog3Id() != null))) {
//			boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
//		}
//		// 属性查询
//		if (param.getAttrs() != null && param.getAttrs().size() > 0) {
//			for (String attrStr : param.getAttrs()) {
//				BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//				String[] s = attrStr.split("-");
//				// 检索的id,属性检索的值
//				String attrId = s[0];
//				String[] attrValue = s[1].split(":");
//				boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
//				boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", attrValue));
//				// 构建一个嵌入式Query,每一个必须都得生成嵌入式的nested查询
//				NestedQueryBuilder attrsQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
//				boolQuery.filter(attrsQuery);
//			}
//		}
//		// 1.2 bool - filter [库存]
//		if (param.getHasStock() != null) {
//			boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
//		}
//		// 1.2 bool -filter [价格区间]
//		if (StringUtils.isNotEmpty(param.getSkuPrice())) {
//			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
//			String[] s = param.getSkuPrice().split("_");
//			if (s.length == 2) {
//				// 有三个值就有区间
//				rangeQueryBuilder.gte(s[0]).lte(s[1]);
//			} else if (s.length == 1) {
//				// 单值情况，单有有上线或者下线
//				if (param.getSkuPrice().startsWith("_")) {
//					rangeQueryBuilder.lte(s[0]);
//				}
//				if (param.getSkuPrice().endsWith("_")) {
//					rangeQueryBuilder.gte(s[0]);
//				}
//			}
//			boolQuery.filter(rangeQueryBuilder);
//		}
//
//		// 将前面的查询情况封装进来
//		sourceBuilder.query(boolQuery);
//
//		/**
//		 * @Author: stone
//		 * 排序，分页，高亮
//		 **/
//		// 1. 排序
//		if (StringUtils.isNotEmpty(param.getSort())) {
//			String sort = param.getSort();
//			// sort= hotScore_asc/desc
//			String[] s = sort.split("_");
//			SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
//			sourceBuilder.sort(s[0], order);
//		}
//
//		// 2. 分页
//		sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
//		sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
//
//		// 3. 高亮
//		if (StringUtils.isNotEmpty(param.getKeyword())) {
//			HighlightBuilder builder = new HighlightBuilder();
//			builder.field("skuTitle");
//			builder.preTags("<b style='color:red'>");
//			builder.encoder("</b>");
//			sourceBuilder.highlighter(builder);
//		}
//		// 聚合分析
//		// 1. 品牌聚合
//		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
//		brand_agg.field("brandId").size(50);
//		// 品牌聚合的子聚合
//		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
//		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
//		sourceBuilder.aggregation(brand_agg);
//		// 分类聚合
//		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
//		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
//		// 将分类聚合加入 sourceBuilder
//		sourceBuilder.aggregation(catalog_agg);
//		// 3.属性聚合 attr_agg 构建嵌入式聚合
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
//		log.info("\n构建语句：->\n" + sourceBuilder.toString());
//		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
//		return searchRequest;
//	}
//
//
//	/**
//	 * @Author: stone
//	 * @Description: 构建结果数据 指定catalogId 、brandId、attrs.attrId、嵌入式查询、倒序、0-6000、每页显示两个、高亮skuTitle、聚合分析
//	 *
//	 * @return*/
//	private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
//		SearchResult result = new SearchResult();
//		// 1.返回的所有查询到的商品
//		SearchHits hits = response.getHits();
//
//		List<SkuEsModel> esModels = new ArrayList<>();
//		if (hits.getHits() != null && hits.getHits().length > 0) {
//			for (SearchHit hit : hits.getHits()) {
//				String sourceAsString = hit.getSourceAsString();
//				// ES中检索得到的对象
//				SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
//				if (!StringUtils.isEmpty(param.getKeyword())) {
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
//		// 2.当前所有商品涉及到的所有属性信息
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
//		// 3.当前所有商品涉及到的所有品牌信息
//		ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
//		ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
//		for (Terms.Bucket bucket : brand_agg.getBuckets()) {
//			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
//			// 3.1 得到品牌的id
//			long brnadId = bucket.getKeyAsNumber().longValue();
//			brandVo.setBrandId(brnadId);
//			// 3.2 得到品牌的名
//			String brand_name = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
//			brandVo.setBrandName(brand_name);
//			// 3.3 得到品牌的图片
//			String brand_img = ((ParsedStringTerms) (bucket.getAggregations().get("brand_img_agg"))).getBuckets().get(0).getKeyAsString();
//			brandVo.setBrandImg(brand_img);
//			brandVos.add(brandVo);
//		}
//		result.setBrands(brandVos);
//
//		// 4.当前商品所有涉及到的分类信息
//		ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
//		List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
//		for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
//			// 设置分类id
//			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
//			catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
//			// 得到分类名
//			ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
//			String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
//			catalogVo.setCatalogName(catalog_name);
//			catalogVos.add(catalogVo);
//		}
//		result.setCatalogs(catalogVos);
//		// ================以上信息从聚合信息中获取
//		// 5.分页信息-页码
//		result.setPageNum(param.getPageNum());
//
//		// 总记录数
//		long total = hits.getTotalHits().value;
//
//		result.setTotal(total);
//
//		// 总页码：计算得到
//		int totalPages = (int) (total / EsConstant.PRODUCT_PAGESIZE + 0.999999999999);
//		result.setTotalPages(totalPages);
//		// 设置导航页
//		ArrayList<Integer> pageNavs = new ArrayList<>();
//		for (int i = 1; i <= totalPages; i++) {
//			pageNavs.add(i);
//		}
//		result.setPageNavs(pageNavs);
//
//		// 6.构建面包屑导航功能
//		if (param.getAttrs() != null) {
//			List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
//				SearchResult.NavVo navVo = new SearchResult.NavVo();
//				String[] s = attr.split("_");
//				navVo.setNavValue(s[1]);
//				R r = productFeignService.getAttrsInfo(Long.parseLong(s[0]));
//				// 将已选择的请求参数添加进去 前端页面进行排除
//				result.getAttrIds().add(Long.parseLong(s[0]));
//				if (r.getCode() == 0) {
//					AttrResponseVo data = r.getData(new TypeReference<AttrResponseVo>() {
//					});
//					navVo.setName(data.getAttrName());
//				} else {
//					// 失败了就拿id作为名字
//					navVo.setName(s[0]);
//				}
//				// 拿到所有查询条件 替换查询条件
//				String replace = replaceQueryString(param, attr, "attrs");
//				navVo.setLink("http://search.gulimall.com/list.html?" + replace);
//				return navVo;
//			}).collect(Collectors.toList());
//			result.setNavs(navVos);
//		}
//
//		// 品牌、分类
//		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
//			List<SearchResult.NavVo> navs = result.getNavs();
//			SearchResult.NavVo navVo = new SearchResult.NavVo();
//			navVo.setName("品牌");
//			// TODO 远程查询所有品牌
//			R r = productFeignService.brandInfo(param.getBrandId());
//			if (r.getCode() == 0) {
//				List<BrandVo> brand = r.getData("data", new TypeReference<List<BrandVo>>() {
//				});
//				StringBuffer buffer = new StringBuffer();
//				// 替换所有品牌ID
//				String replace = "";
//				for (BrandVo brandVo : brand) {
//					buffer.append(brandVo.getBrandName() + ";");
//					replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
//				}
//				navVo.setNavValue(buffer.toString());
//				navVo.setLink("http://search.gulimall.com/list.html?" + replace);
//			}
//			navs.add(navVo);
//		}
//		return result;
//	}
//
//	/**
//	 * 替换字符
//	 * key ：需要替换的key
//	 */
//	private String replaceQueryString(SearchParam param, String value, String key) {
//		String encode = null;
//		try {
//			encode = URLEncoder.encode(value, "UTF-8");
//			// 浏览器对空格的编码和java的不一样
//			encode = encode.replace("+", "%20");
//			encode = encode.replace("%28", "(").replace("%29", ")");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return param.get_queryString().replace("&" + key + "=" + encode, "");
//	}
		return null;
	}
}
