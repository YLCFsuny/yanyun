package com.hmall.item.es;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(properties = "spring.profiles.active=local")
//@EnableAutoConfiguration(exclude = {SeataAutoConfiguration.class}) // 排除Seata自动配置
public class PageTest {

    @Autowired
    private ItemService itemService;

    @Test
    public void test() {
        int pageNo = 1;
        int pageSize = 10;
        Page<Item> page = itemService.lambdaQuery()
                .eq(Item::getStatus, 1)
                .page(Page.of(pageNo, pageSize));
        List<Item> records = page.getRecords();
        System.out.println("item = " + 1);
        for (Item item : records) {
            System.out.println("item = " + item);
        }
    }
}
