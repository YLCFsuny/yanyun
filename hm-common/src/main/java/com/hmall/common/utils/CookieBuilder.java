package com.hmall.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Data
@Accessors(chain = true, fluent = true)
public class CookieBuilder {
    private Charset charset = StandardCharsets.UTF_8;  // 编码方式，默认为UTF-8
    private int maxAge = -1;
    private String path = "/";
    private boolean httpOnly;
    private String name;
    private String value;
    private String domain;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    // 构造函数，接收HttpServletRequest和HttpServletResponse对象
    public CookieBuilder(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * 构建cookie，会对cookie值用UTF-8做URL编码，避免中文乱码
     */
    public void build(){
        if (response == null) {
            log.error("response为null，无法写入cookie");
            return;
        }
        // 对cookie值进行URL编码
        Cookie cookie = new Cookie(name, URLEncoder.encode(value, charset));  // 创建一个新的Cookie对象，将name和value作为参数传入
        if(StrUtil.isNotBlank(domain)) {  // 若指定了domain，则直接使用指定的domain；domain是一个域名，如：.baidu.com
            cookie.setDomain(domain);
        }else if (request != null) {  // 若没有指定domain，则使用当前服务器的域名
            String serverName = request.getServerName();
            serverName = StrUtil.subAfter(serverName, ".", false);  // 截取域名的后缀部分
            cookie.setDomain("." + serverName);  // 设置cookie的domain为当前服务器的域名
        }
        cookie.setHttpOnly(httpOnly);  // 设置cookie是否只能通过HTTP访问，不能通过JavaScript访问
        cookie.setMaxAge(maxAge);  // 设置cookie的最大存活时间，单位为秒
        cookie.setPath(path);  // 设置cookie的路径，默认为"/"，即整个网站都可以访问该cookie
        log.debug("生成cookie，编码方式:{}，【{}={}，domain:{};maxAge={};path={};httpOnly={}】",
                charset.name(), name, value, domain, maxAge, path, httpOnly);
        response.addCookie(cookie);
    }

    /**
     * 利用UTF-8对cookie值解码，避免中文乱码问题
     * @param cookieValue cookie原始值
     * @return 解码后的值
     */
    // 解码cookie值的方法，用于将URL编码的cookie值解码为原始值
    // 解码时使用UTF-8编码，以避免中文乱码问题
    // 解码后的值将作为cookie的实际值使用
    public String decode(String cookieValue){

        return URLDecoder.decode(cookieValue, charset);  // 使用URLDecoder的decode方法对cookie值进行解码，解码时使用指定的编码方式
    }
}
