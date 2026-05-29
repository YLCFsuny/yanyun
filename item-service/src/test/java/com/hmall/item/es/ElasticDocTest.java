package com.hmall.item.es;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.json.JSONUtil;
import com.hmall.common.utils.BeanUtils;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.ItemService;
import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 文档操作
 */
@SpringBootTest(properties = "spring.profiles.active=local")
//@EnableAutoConfiguration(exclude = SeataAutoConfiguration.class) // 禁用Seata自动配置
public class ElasticDocTest {

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ItemService itemService;
    /**
     * 导入索引库数据（文档）
     */
    @Test
    void testIndexDoc() throws IOException {
        //准备文档数据
        Item item = itemService.getById(584382);
        ItemDoc itemDoc = BeanUtils.copyProperties(item, ItemDoc.class);
        //1. 准备Request对象
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());
        //2. 准备请求参数
        request.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
        //3. 发送请求
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        //输出响应结果
        System.out.println("index = " + index);
    }


     // * 查询索引库数据（文档）

    @Test
    void testGetIndex() throws IOException {
        //准备Request对象
        GetRequest request = new GetRequest("items", "584382");
        //发送请求
        GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        //解析响应结果
        String sourceAsString = documentFields.getSourceAsString();
        ItemDoc itemDoc = JSONUtil.toBean(sourceAsString, ItemDoc.class);
        System.out.println("itemDoc = " + itemDoc);
    }

     // * 更新索引库数据（文档）

    @Test
    void testUpdateIndex() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        //准备Request对象
        UpdateRequest request = new UpdateRequest("items", "584382");
        //准备请求参数
//        request.doc("price", 1000, "updateTime", LocalDateTime.now());
        request.doc("price", 1000, "updateTime", timestamp);
        //发送请求
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    // 删除索引库数据（文档）

    @Test
    void testDeleteIndex() throws IOException {
        //准备Request对象
        DeleteRequest request = new DeleteRequest("items", "1");
        //发送请求
        restHighLevelClient.delete(request, RequestOptions.DEFAULT);
    }

    // 批量操作（导入）索引库数据（文档）

    @Test
    void testBulkDoc() throws IOException {
        int pageNo = 1;
        int pageSize = 500;
        while (true) {
            //准备文档数据
            Page<Item> page = itemService.lambdaQuery()
                    .eq(Item::getStatus, 1)
                    .page(Page.of(pageNo, pageSize));
            List<Item> records = page.getRecords();
            if (records.isEmpty()) {
                return;
            }
            //准备Request对象
            BulkRequest request = new BulkRequest();
            //准备请求参数
            ItemDoc itemDoc = null;
            for (Item item : records) {
//                itemDoc = BeanUtils.toBean(itemDoc, ItemDoc.class);
                itemDoc = BeanUtils.copyProperties(item, ItemDoc.class);
                request.add(new IndexRequest("items").id(itemDoc.getId())
                       .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON));
            }
//            request.add(new DeleteRequest("items", itemDoc.getId()));
//            request.add(new UpdateRequest("items", itemDoc.getId())
//                   .doc(JSONUtil.toJsonStr(itemDoc), XContentType.JSON));
            //发送请求
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            //翻页
            pageNo++;
        }
    }

    ///   //////////////////////////////////////////////////////////////////////////////////

    @BeforeEach
    public void setUp() {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(
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
