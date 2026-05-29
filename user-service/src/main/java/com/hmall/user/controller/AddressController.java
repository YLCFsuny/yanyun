package com.hmall.user.controller;


import com.hmall.common.exception.BadRequestException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.UserContext;
import com.hmall.user.domain.dto.AddressDTO;
import com.hmall.user.domain.po.Address;
import com.hmall.user.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 */
@RestController  // 响应json数据  相当于 @Controller + @ResponseBody
@RequestMapping("/addresses")  // 路径前缀
@RequiredArgsConstructor  // 生成构造器 注入 IAddressService
@Api(tags = "收货地址管理接口")  // 描述接口的分类
public class AddressController {

    private final IAddressService addressService;

    @ApiOperation("根据id查询地址")  // 描述接口的功能
    @GetMapping("{addressId}")  // 路径参数
    public AddressDTO findAddressById(@ApiParam("地址id") // 描述参数的含义
                                          @PathVariable("addressId") Long id // 表示从路径中获取addressId的值
                                      ) {
        // 1.根据id查询
        Address address = addressService.getById(id);
        // 2.判断当前用户
        Long userId = UserContext.getUser();
        if(!address.getUserId().equals(userId)){  // 只能查询自己的地址
            throw new BadRequestException("地址不属于当前登录用户");
        }
        return BeanUtils.copyBean(address, AddressDTO.class);  // 转vo
    }
    @ApiOperation("查询当前用户地址列表")
    @GetMapping
    public List<AddressDTO> findMyAddresses() {
        // 1.查询列表
        List<Address> list = addressService.query().eq("user_id",
                                                       UserContext.getUser()  // 获取当前登录用户的id
                                                        )  // 条件查询，只查询当前用户的地址
                                                        .list();  // 查询列表
        // 2.判空
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 3.转vo
        return BeanUtils.copyList(list, AddressDTO.class);
    }
}
