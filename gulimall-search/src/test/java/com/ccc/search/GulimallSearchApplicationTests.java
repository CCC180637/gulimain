package com.ccc.search;

import com.alibaba.fastjson.JSON;
import com.ccc.search.confing.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {
        System.out.println(client);
    }


    /**
     * 测试存储数据到es
     * 更新也可以用
     *
     * @throws IOException
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");   //数据的id
        User user = new User();
        user.setUserName("ccc");
        user.setAge(19);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);  //要保存的内容
        //执行操作
        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        //提取有用的响应数据
        System.out.println(index);

    }

    /**
     * 测试复杂检索
     * @throws IOException
     */
    @Test
    public void searchData() throws IOException {
        //1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL ，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.1)构建检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        //1.2)按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);

        //1.3)、计算平均薪资
        AvgAggregationBuilder balance = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balance);

        searchRequest.source(sourceBuilder);

        //2.执行检索
        SearchResponse search = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        //3、分析结果
        System.out.println(search.toString());

        //3.1)、获取所有查询到的数据
        SearchHits hits = search.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String string = hit.getSourceAsString();
            Account account = JSON.parseObject(string, Account.class);
            System.out.println("account："+account);
        }

        //3.2)、获取这次检索到的分析信息
        Aggregations aggregations = search.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket: ageAgg1.getBuckets()){
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+keyAsString);
        }
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资："+balanceAvg.getValue());
    }


    @Data
   static class User {
        private String userName;
        private String gender;
        private Integer age;
    }


    @ToString
    @Data
   static class Account {
        private int      account_number;
        private int      balance;
        private String   firstname;
        private String   lastname;
        private String   age;
        private String   gender;
        private String   address;
        private String   employer;
        private String   email;
        private String   city;
        private String   state;

    }

}
