package com.hmall.common.exception;


// 自定义异常类，用于表示请求参数错误的异常
// 继承自 CommonException 类，该类是一个通用的异常类，用于封装异常信息和状态码
// 状态码为 400，表示请求参数错误
public class BadRequestException extends CommonException{

    // 构造函数，用于创建 BadRequestException 对象
    // 调用父类的构造函数，传入异常信息和状态码
    public BadRequestException(String message) {
        super(message, 400);
    }

    // 构造函数，用于创建 BadRequestException 对象
    // 调用父类的构造函数，传入异常信息、异常原因和状态码
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, 400);
    }

    // 构造函数，用于创建 BadRequestException 对象
    // 调用父类的构造函数，传入异常原因和状态码
    public BadRequestException(Throwable cause) {

        super(cause, 400);
    }
}
