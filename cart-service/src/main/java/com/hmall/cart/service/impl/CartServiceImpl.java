package com.hmall.cart.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.cart.config.CartProperties;
import com.hmall.cart.domain.dto.CartFormDTO;
import com.hmall.cart.domain.po.Cart;
import com.hmall.cart.domain.vo.CartVO;
import com.hmall.cart.mapper.CartMapper;
import com.hmall.cart.service.ICartService;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 */
@Slf4j
@Service  // 注册为Spring的Bean，用于处理业务逻辑
@RequiredArgsConstructor  // 自动注入构造器
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private final CartMapper cartMapper;
//    // 单体架构，直接注入bean
//    private final IItemService itemService;
//    // 分布式微服务架构，注入 RestTemplate，发送http请求，用于远程调用其他服务，但是网络请求端口固定
//    private final RestTemplate restTemplate;
//    // nacos注册中心，用于获取服务的实例列表。但是代码书写复杂，需要手动获取实例列表，然后进行负载均衡，再发起http请求。
//    private final DiscoveryClient discoveryClient;
//
//     注入feign客户端，用于远程调用其他服务，代码书写简单，自动获取服务的实例列表，然后进行负载均衡，再发起http请求。
//     注意：feign客户端需要在启动类上添加@EnableFeignClients注解，才能生效。
//     默认使用HttpURLConnection发送请求，不支持连接池，需要在配置文件中添加配置，才能使用okhttp（支持连接池）。
    private final ItemClient itemClient;  // 商品服务

    private final CartProperties cartProperties;  // 购物车配置

    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {  // 新增购物车条目
        // 1.获取登录用户
        log.debug("当前登录用户：{}", UserContext.getUser());
        Long userId = UserContext.getUser();

        // 2.判断是否已经存在
        if(checkItemExists(cartFormDTO.getItemId(), userId)){
            // 2.1.存在，则更新数量
//            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            cartMapper.updateNum(cartFormDTO.getItemId(), userId);
            return;
        }
        // 2.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);

        // 3.新增购物车条目
        // 3.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 3.2.保存当前用户
        cart.setUserId(userId);
        // 3.3.保存到数据库
        save(cart);
    }

    @Override
    public List<CartVO> queryMyCarts() {  // 查询我的购物车列表
        // 1.查询我的购物车列表
//        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, 1L /*UserContext.getUser()*/).list();
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, UserContext.getUser()).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }
        // 2.转换VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);
        // 3.处理VO中的商品信息
        handleCartItems(vos);
        // 4.返回
        return vos;
    }

    private void handleCartItems(List<CartVO> vos) {  // 处理购物车条目中的商品信息
        // 1.获取商品id
        Set<Long> itemIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());
        // 2.查询商品
        // List<ItemDTO> items = itemService.queryItemByIds(itemIds);

        ////////////////////////////////////////////////////////////////////////////////////////////
//        // 2.1 根据服务名称获取服务的实例列表
//        List<ServiceInstance> instances = discoveryClient.getInstances("item-service");
//        if (CollUtils.isEmpty(itemIds)) {
//            return;
//        }
//        // 2.2 负载均衡，从实例列表中选择一个实例
//        ServiceInstance instance = instances.get(RandomUtil.randomInt(instances.size()));
//        // 2.3 使用restTemplate，发起http请求，得到http响应，查询商品  (反射)
//        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
//                //"http://localhost:8081/items?ids={ids}",
//                instance.getUri() + "/items?ids={ids}",
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<ItemDTO>>() {},
//                Map.of("ids", CollUtils.join(itemIds, ","))
//        );
//        // 2.4 从响应中获取商品信息
//        if (response.getStatusCode().isError()) {
//            throw new BizIllegalException("商品服务异常");
// //            return;
//        }
//        List<ItemDTO> items = response.getBody();
        //////////////////////////////////////////////////////////////////////////////////////

        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);  // 远程调用商品微服务///////////////////////////////////

        if (CollUtils.isEmpty(items)) {
            return;
        }
        // 3.转为 id 到 item 的 map
        Map<Long, ItemDTO> itemMap = items.stream().collect(Collectors.toMap(ItemDTO::getId, Function.identity()));
        // 4.写入vo
        for (CartVO v : vos) {
            ItemDTO item = itemMap.get(v.getItemId());
            if (item == null) {
                continue;
            }
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUser())
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }

    private void checkCartsFull(Long userId) {
        int count = Math.toIntExact(lambdaQuery().eq(Cart::getUserId, userId).count());   // .count();
        if (count >= cartProperties.getMaxItems()) {
            throw new BizIllegalException(StrUtil.format("用户购物车商品数量不能超过{}", cartProperties.getMaxItems()));
        }
    }

    private boolean checkItemExists(Long itemId, Long userId) {
//        int count = lambdaQuery()
        int count = Math.toIntExact(lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count());
        return count > 0;
    }
}
