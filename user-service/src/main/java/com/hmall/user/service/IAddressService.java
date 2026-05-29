package com.hmall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.user.domain.po.Address;


// 服务层接口
// 继承了MybatisPlus的IService接口，提供了一些基本的CRUD操作
// 泛型参数T是实体类的类型，这里是Address
// 可以根据需要添加自定义的方法，如分页查询、条件查询等
// 注意：这里的方法名和参数类型需要和Mapper层的方法名和参数类型一致
// 否则会导致MybatisPlus无法自动生成对应的SQL语句
public interface IAddressService extends IService<Address> {}
