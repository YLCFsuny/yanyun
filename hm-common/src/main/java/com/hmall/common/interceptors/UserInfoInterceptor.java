package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpInterceptor;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hmall.common.exception.UnauthorizedException;
import com.hmall.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//拦截器，用于获取用户信息
@Component  //将该类声明为Spring的组件，使其成为一个Bean
//@Order(1)  //指定拦截器的执行顺序
@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    //在请求处理之前进行调用（Controller方法调用之前）
    //返回值：true表示继续流程（如调用下一个拦截器或Controller方法）；false表示中断流程（如登录检查失败）
    public boolean preHandle(HttpServletRequest request,   //请求对象
                             HttpServletResponse response, //响应对象
                             Object handler  //处理器（如Controller方法）
                            ) throws Exception {

        //获取登录用户信息
        String userInfo = request.getHeader("user-info");

        //判断是否获取了用户信息，如果有，存入ThreadLocal中
        // StrUtil 是 Hutool 工具类库中的一个工具类，用于字符串处理
        // StrUtil.isNotBlank(userInfo) 检查 userInfo 是否不为空且不为空白字符（空格、制表符、换行符等）。
        if (StrUtil.isNotBlank(userInfo)){
            //存入ThreadLocal中
            UserContext.setUser(Long.valueOf(userInfo));
        }
//      else {
//            throw new UnauthorizedException("未登录");
//      }

        //放行
        return true;

    }

    @Override  //在请求处理完之后进行调用（Controller方法调用之后）
    //但仅在preHandle方法返回true时才会被调用
    //通常用于清理资源、记录日志等操作
    public void afterCompletion(HttpServletRequest request,     //请求对象
                                HttpServletResponse response,   //响应对象
                                Object handler,                 //处理器（如Controller方法）
                                Exception ex                    //异常对象（如果有的话）
                                ) throws Exception {
        //移除ThreadLocal中的用户信息
        UserContext.removeUser();
    }

}
