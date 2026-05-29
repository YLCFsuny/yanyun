package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.utils.BeanUtils;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.service.ItemService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
//@Service
@Transactional
@SpringBootTest(properties = "spring.profiles.active=local")
public class TPTest {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ItemService itemService;

    //    @Autowired
    private RestHighLevelClient client;

    private static final String ARTICLE_ES_INDEX = "items";

    private static final int PAGE_SIZE = 2000;

    /**
     * 批量导入
     */
    @SneakyThrows
    @Test
    public void importAll() {

        //总条数
        int count = itemMapper.selectCount();
        //总页数
        int totalPageSize = count % PAGE_SIZE == 0 ? count / PAGE_SIZE : count / PAGE_SIZE + 1;
        System.out.println("总条数：" + count);
        System.out.println("总页数：" + totalPageSize);
        if (count == 0) {
            log.info("没有需要导入的数据");
            return;
        }
        //开始执行时间
        long startTime = System.currentTimeMillis();
        //一共有多少页，就创建多少个CountDownLatch的计数
        CountDownLatch countDownLatch = new CountDownLatch(totalPageSize);

        Page<Item> itemList;

        for (int i = 0; i < totalPageSize; i++) {
            //查询文章
//            itemList = itemService.page(Page.of(i + 1, PAGE_SIZE));

            itemList = itemService.lambdaQuery()
                        .eq(Item::getStatus, 1)
                        .page(Page.of(i + 1, PAGE_SIZE));

            //创建线程，做批量插入es数据操作
            TaskThread taskThread = new TaskThread(itemList, countDownLatch);
            //执行线程
            executorService.execute(taskThread);
        }

        //调用await()方法,用来等待计数归零
        countDownLatch.await();

        long endTime = System.currentTimeMillis();
        log.info("es索引数据批量导入共:{}条,共消耗时间:{}秒", count, (endTime - startTime) / 1000);
    }

    class TaskThread implements Runnable {

        Page<Item> page;
        CountDownLatch countDownLatch;

        private final List<Item> itemList;

        public TaskThread(Page<Item> page, CountDownLatch countDownLatch) {
            this.page = page;
            this.countDownLatch = countDownLatch;

            this.itemList = page.getRecords();
        }

        @SneakyThrows
        @Override
        public void run() {
            try {
                if (itemList == null || itemList.isEmpty()) {
                    log.info("当前页无数据，跳过导入");
                    return;
                }
                BulkRequest bulkRequest = new BulkRequest();
                // 构建批量请求
                ItemDoc itemDoc;
                for (Item item : itemList) {
                    itemDoc = BeanUtils.copyProperties(item, ItemDoc.class);
                    IndexRequest indexRequest = new IndexRequest(ARTICLE_ES_INDEX)
                            .id(itemDoc.getId())
                            .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }
                // 执行批量导入
                BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);

                if (response.hasFailures()) {
                    log.error("批量导入失败: {}", response.buildFailureMessage());
                } else {
                    log.info("成功导入{}条数据", itemList.size());
                }

            } catch (Exception e) {
                log.error("数据导入异常", e);
            } finally {
                // 确保计数减一
                countDownLatch.countDown();
                System.out.println(countDownLatch.getCount());
            }
        }
    }

    ///   //////////////////////////////////////////////////////////////////////////////////

    @BeforeEach
    public void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.100.128:9200")
        ));
    }
    @AfterEach
    public void tearDown() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}

