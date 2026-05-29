package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ElasticSearchTest {
    private RestHighLevelClient restHighLevelClient;
    /**
     * 搜索查询
     */
    @Test
    void testSearch() throws IOException {
        int pageNo = 1, pageSize = 5;
        //准备Request对象
        SearchRequest request = new SearchRequest("items");
        //准备请求参数
//        request.source().query(QueryBuilders.termQuery("title", "小米"));
//        request.source().query(QueryBuilders.matchAllQuery()).from(0).size(5);
        request.source()
                //查询条件
                .query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("name", "手机")))
//                        .must(QueryBuilders.termQuery("brand", "小米"))
//                        .filter(QueryBuilders.rangeQuery("price").gt(1000).lt(2000))
//                        .mustNot(QueryBuilders.termQuery("status", 0))
//                        .should(QueryBuilders.termQuery("brand", "华为")))
//                //分页
//                .from((pageNo-1)*pageSize).size(pageSize)
                //排序
                .sort("price", SortOrder.DESC).sort("sold", SortOrder.ASC)
                //高亮
                .highlighter(SearchSourceBuilder.highlight().field("name").preTags("<font color='red'>").postTags("</font>"));
        //发送请求
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //解析响应结果
        extracted(search);
    }
    /**
     * 聚合查询
     */
    @Test
    void testAgg() throws IOException {
        //准备Request对象
        SearchRequest request = new SearchRequest("items");
        //准备请求参数
        request.source()
                .size(0)  // 分页参数，返回文档数量为零
                .aggregation(
                        AggregationBuilders
                            .terms("priceAgg").field("price").size(10)
                            .subAggregation(  // 子聚合
                                    AggregationBuilders
                                            .stats("priceStats").field("price")
                            )
                );
        //发送请求
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //解析响应结果terms
        Aggregations aggregations = search.getAggregations();
        Terms priceTerms = aggregations.get("priceAgg");
        List<? extends Terms.Bucket> buckets = priceTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String price = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println("price = " + price + ", docCount = " + docCount);
            // 获取价格统计信息
            Stats priceStats = bucket.getAggregations().get("priceStats");
            if (priceStats != null) {
                System.out.println("  Average Price: " + priceStats.getAvg());
                System.out.println("  Max Price: " + priceStats.getMax());
                System.out.println("  Min Price: " + priceStats.getMin());
            }
//            // 解析响应结果stats
//            Aggregation priceStats = bucket.getAggregations().get("priceStats");
//            Map<String, Object> priceStatsMetadata = priceStats.getMetadata();
//            Set<String> strings = priceStatsMetadata.keySet();
//            // priceStatsMetadata.valueSet();
//            for (Map.Entry<String, Object> s : priceStatsMetadata.entrySet()) {
//                Object key = s.getKey();
//                Object value = s.getValue();
//                System.out.println(key + " = " + value);
//                System.out.println("key = " + key + ", value = " + value);
//            }
        }
    }
    /**
     * 聚合查询
     */
    @Test
    void testPriceAggregation() throws IOException {
        // 1. 准备请求
        SearchRequest request = new SearchRequest("items");
        // 2. 构建聚合 - 更合理的聚合结构
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .size(0)  // 分页参数，返回文档数量为零
                .aggregation(
                        AggregationBuilders
                                .terms("priceRanges")  // 更合适的聚合名称
                                .field("price")   // 对price字段进行分桶
                                .size(10)  // 最多返回10个桶
                                .subAggregation(  // 子聚合
                                        AggregationBuilders
                                                .stats("priceStats").field("price")  // 对每个桶内的price做统计
                                )
                );
        request.source(sourceBuilder);
        // 3. 执行查询
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        // 4. 安全解析结果
        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            Terms priceRanges = aggregations.get("priceRanges");
            if (priceRanges != null) {
                for (Terms.Bucket bucket : priceRanges.getBuckets()) {
                    // 获取价格区间和文档数
                    String priceRange = bucket.getKeyAsString();
                    long docCount = bucket.getDocCount();
                    System.out.println("Price Range: " + priceRange + ", Count: " + docCount);
                    // 获取价格统计信息
                    Stats priceStats = bucket.getAggregations().get("priceStats");
                    if (priceStats != null) {
                        System.out.println("  Average Price: " + priceStats.getAvg());
                        System.out.println("  Max Price: " + priceStats.getMax());
                        System.out.println("  Min Price: " + priceStats.getMin());
                        // 不需要直接访问metadata，使用get方法更安全
                        // Map<String, Object> meta = priceStats.getMetadata(); // 通常不需要
                    }
                }
            }
        }
    }
    /**
     * 解析响应结果
     */
    private static void extracted(SearchResponse search) {
        SearchHits hits = search.getHits();
        // 总条数
        Assertions.assertNotNull(hits.getTotalHits());  // 断言，判断是否为空
        long value = hits.getTotalHits().value;
        System.out.println("value = " + value);
        // 命中的数据
        SearchHit[] hit = hits.getHits();
        System.out.println("hit = " + Arrays.toString(hit));
        for (SearchHit hitI : hit) {
            String sourceAsString = hitI.getSourceAsString();
            System.out.println("sourceAsString = " + sourceAsString);
            // 转为ItemDoc对象
            ItemDoc itemDoc = JSONUtil.toBean(sourceAsString, ItemDoc.class);
            // 获取高亮结果
            Map<String, HighlightField> highlightFields = hitI.getHighlightFields();
            if (highlightFields != null) {
                // 根据高亮字段获取高亮结果
                HighlightField hf = highlightFields.get("name");
                Text[] fragments = hf.getFragments();
                StringBuilder shfb = new StringBuilder();
                // 拼接高亮结果
                for (Text fragment : fragments) {
                    String shf = fragment.string();
                    shfb.append(shf);
                }
                String shf = shfb.toString();
//                String shf = hf.getFragments()[0].string();
                itemDoc.setName(shf);
            }
            System.out.println("结果");
            System.out.println("itemDoc = " + itemDoc);
        }
    }

    ///  /////////////////////////////////////////////////////////////////////////////////////////

    @BeforeEach
    public void setUp() {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(  // 连接ES
                HttpHost.create("http://192.168.100.128:9200")
        ));
    }
    @AfterEach
    public void tearDown() throws IOException {
        if (restHighLevelClient != null) {
            restHighLevelClient.close();
        }
    }
}
