package com.stone.mall.search;

import com.alibaba.fastjson.JSON;
import com.mysql.cj.QueryBindings;
import com.stone.mall.search.config.MallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Test
    void indexTest() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        user.setGender("男");
        String JsonUser = JSON.toJSONString(user);
        indexRequest.source(JsonUser, XContentType.JSON);

        // 执行操作
        IndexResponse indexResponse = client.index(indexRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        // 提取有用的响应数据
        System.out.println(indexResponse);

    }

    @Data
    class User {
        private String name;
        private String gender;
        private Integer age;
    }

    @Test
    void searchData() throws IOException {
        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        System.out.println(sourceBuilder.toString());

        searchRequest.source(sourceBuilder);

        // 执行检索
        SearchResponse request = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(request.toString());
    }

}
