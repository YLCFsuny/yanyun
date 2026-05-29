package com.hmall.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.item.domain.po.Item;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 */
public interface ItemMapper extends BaseMapper<Item> {

    @Select("SELECT COUNT(*) FROM item")
    public int selectCount();

    @Update("UPDATE item SET stock = stock - #{num} WHERE id = #{itemId}")
    public void updateStock(OrderDetailDTO orderDetail);
}
