# Hmall - Java Spring Cloud 微服务项目

一个基于 Spring Cloud 的电商微服务项目，包含用户、商品、购物车、订单、支付等核心模块。

## 项目结构

```
hmall/
├── cart-service/        # 购物车服务
├── eureka-server/       # Eureka 注册中心
├── hm-api/             # API 接口模块
├── hm-common/          # 公共模块
├── hm-gateway/         # 网关服务
├── item-service/        # 商品服务
├── pay-service/         # 支付服务
├── trade-service/       # 交易服务
├── user-service/        # 用户服务
└── pom.xml              # 父 POM
```

## 技术栈

- **微服务框架**：Spring Cloud
- **服务注册**：Eureka
- **服务网关**：Spring Cloud Gateway
- **服务调用**：OpenFeign
- **分布式事务**：Seata
- **消息队列**：RabbitMQ
- **数据库**：MySQL
- **ORM**：MyBatis-Plus
- **搜索**：Elasticsearch

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- RabbitMQ 3.7+
- Elasticsearch 7.x

### 启动步骤

1. **启动 Eureka 注册中心**
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

2. **启动各个微服务**
   ```bash
   # 用户服务
   cd user-service
   mvn spring-boot:run

   # 商品服务
   cd item-service
   mvn spring-boot:run

   # 购物车服务
   cd cart-service
   mvn spring-boot:run

   # 交易服务
   cd trade-service
   mvn spring-boot:run

   # 支付服务
   cd pay-service
   mvn spring-boot:run

   # 网关服务
   cd hm-gateway
   mvn spring-boot:run
   ```

3. **访问应用**
   - Eureka 控制台：http://localhost:8761
   - 网关入口：http://localhost:8080

### 数据库初始化

执行 `hmall.sql` 创建数据库和表结构。

## 服务端口

| 服务 | 端口 |
|------|------|
| Eureka Server | 8761 |
| Gateway | 8080 |
| User Service | 8081 |
| Item Service | 8082 |
| Cart Service | 8083 |
| Trade Service | 8084 |
| Pay Service | 8085 |

## 功能模块

- **用户模块**：注册、登录、地址管理
- **商品模块**：商品列表、搜索、详情
- **购物车模块**：添加、删除、修改购物车
- **订单模块**：下单、订单查询、订单状态管理
- **支付模块**：支付、退款、支付回调

## 开发说明

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 统一异常处理

### 分支管理

- `master`：主分支，稳定版本
- `develop`：开发分支
- `feature/*`：功能分支
- `bugfix/*`：修复分支

## 许可证

MIT License
