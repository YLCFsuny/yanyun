package com.hmall.item.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 *索引库操作
 */
public class ElasticTest {

    private RestHighLevelClient restHighLevelClient;
    /**
     * 连接
     */
    @BeforeEach
    public void setUp() {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.100.128:9200")
        ));
    }
    /**
     * 测试连接
     */
    @Test
    public void testConnection() {
        System.out.println("restHighLevelClient = " + restHighLevelClient);
    }
    /**
     * 创建索引数据
     * @throws IOException
     */
    @Test
    void testCreateIndex() throws IOException {
        //准备Request对象
        CreateIndexRequest request = new CreateIndexRequest("items");
        //准备请求参数
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        //发送请求
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }
    /**
     * 查询索引数据
     * @throws IOException
     * */
    @Test
    void testGetIndex() throws IOException {
        //准备Request对象
        GetIndexRequest request = new GetIndexRequest("items");
        //发送请求
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
        GetIndexResponse items = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        System.out.println(items);
    }
    /**
     * 删除索引数据
     * @throws IOException
     * */
    @Test
    void testDeleteIndex() throws IOException {
        //准备Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("items");
        //发送请求
        restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
    }
    /**
     * 关闭连接
     */
    @AfterEach
    public void tearDown() throws IOException {
        if (restHighLevelClient != null) {
            restHighLevelClient.close();
        }
    }

    private static final String MAPPING_TEMPLATE = "{\n" +
            "                \"mappings\": {\n" +
            "                    \"properties\": {\n" +
            "                        \"id\": {\n" +
            "                            \"type\": \"keyword\"\n" +
            "                        },\n" +
            "                        \"title\": {\n" +
            "                            \"type\": \"text\",\n" +
            "                            \"analyzer\": \"ik_max_word\",\n" +
            "                            \"search_analyzer\": \"ik_smart\"\n" +
            "                        },\n" +
            "                        \"sellPoint\": {\n" +
            "                            \"type\": \"text\",\n" +
            "                            \"analyzer\": \"ik_max_word\",\n" +
            "                            \"search_analyzer\": \"ik_smart\"\n" +
            "                        },\n" +
            "                        \"price\": {\n" +
            "                            \"type\": \"long\"\n" +
            "                        },\n" +
            "                        \"image\": {\n" +
            "                            \"type\": \"keyword\"\n" +
            "                        },\n" +
            "                        \"cid\": {\n" +
            "                            \"type\": \"long\"\n" +
            "                        },\n" +
            "                        \"status\": {\n" +
            "                            \"type\": \"long\"\n" +
            "                        },\n" +
            "                        \"created\": {\n" +
            "                            \"type\": \"date\"\n" +
            "                        }\n" +
            "                    }   \n" +
            "                }\n" +
            "            }";
    /// ///////////////////////////////////////////////////////////////////////////////////////////////
}
