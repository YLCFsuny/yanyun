package com.hmall.common.utils;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class WebUtils {

    /**
     * 获取ServletRequestAttributes
     *
     * @return ServletRequestAttributes
     */
    // ServletRequestAttributes 是 Spring MVC 框架提供的一个类，用于获取和操作当前请求的相关信息，包括请求对象、响应对象、会话对象等。
    // 它实现了 RequestAttributes 接口，提供了获取和设置请求属性的方法。
    // 在 Spring MVC 中，当一个请求到达服务器时，会创建一个 ServletRequestAttributes 对象，用于存储和操作当前请求的相关信息。
    // 通过 RequestContextHolder 类，可以获取到当前线程中的 ServletRequestAttributes 对象，从而获取和操作当前请求的相关信息。
    // 例如，可以通过 ServletRequestAttributes 对象获取到当前请求的 HttpServletRequest 对象，从而获取到请求头、请求参数等信息。
    // 同时，ServletRequestAttributes 还提供了一些方法，用于设置和获取请求属性，例如 setAttribute 和 getAttribute 方法。
    // 通过这些方法，可以在请求处理过程中，存储和获取一些自定义的请求属性，从而实现一些自定义的功能。
    public static ServletRequestAttributes getServletRequestAttributes() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            return null;
        }
        return (ServletRequestAttributes) ra;
    }

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    // 这个方法的作用是获取当前请求的 HttpServletRequest 对象。
    // 具体来说，它通过调用 getServletRequestAttributes() 方法获取当前线程中的 ServletRequestAttributes 对象，
    // 然后通过 ServletRequestAttributes 对象的 getRequest() 方法获取到当前请求的 HttpServletRequest 对象。
    // 最后，它将获取到的 HttpServletRequest 对象返回给调用者。
    // 这个方法的作用是方便在 Spring MVC 中获取当前请求的 HttpServletRequest 对象，从而可以方便地获取到请求头、请求参数等信息。
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        return servletRequestAttributes == null ? null : servletRequestAttributes.getRequest();
    }

    /**
     * 获取response
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        return servletRequestAttributes == null ? null : servletRequestAttributes.getResponse();
    }

    /**
     * 获取request header中的内容
     *
     * @param headerName 请求头名称
     * @return 请求头的值
     */
    public static String getHeader(String headerName) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getRequest().getHeader(headerName);
    }

    public static void setResponseHeader(String key, String value){
        HttpServletResponse response = getResponse();
        if (response == null) {
            return;
        }
        response.setHeader(key, value);
    }

    // 这个方法的作用是判断当前请求是否是成功的。
    // 具体来说，它通过调用 getResponse() 方法获取当前请求的 HttpServletResponse 对象，
    // 然后通过 HttpServletResponse 对象的 getStatus() 方法获取到当前响应的状态码。
    // 最后，它将获取到的状态码与 300 进行比较，如果小于 300，则认为当前请求是成功的，返回 true；否则，认为当前请求是失败的，返回 false。
    // 这个方法的作用是方便在 Spring MVC 中判断当前请求是否是成功的，从而可以方便地进行一些后续的处理。
    public static boolean isSuccess() {
        HttpServletResponse response = getResponse();
        return response != null && response.getStatus() < 300;
    }

    /**
     * 获取请求地址中的请求参数组装成 key1=value1&key2=value2
     * 如果key对应多个值，中间使用逗号隔开例如 key1对应value1，key2对应value2，value3， key1=value1&key2=value2,value3
     *
     * @param request
     * @return 返回拼接字符串
     */
    public static String getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();  // 获取请求参数
        return getParameters(parameterMap);  // 调用重载方法，将参数组装成 key1=value1&key2=value2 的形式
    }

    /**
     * 获取请求地址中的请求参数组装成 key1=value1&key2=value2
     * 如果key对应多个值，中间使用逗号隔开例如 key1对应value1，key2对应value2，value3， key1=value1&key2=value2,value3
     *
     * @param queries
     * @return
     */
    public  static <T> String getParameters(final Map<String, T> queries) {
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, T> entry : queries.entrySet()) {
            if(entry.getValue() instanceof String[]){
                buffer.append(entry.getKey()).append(String.join(",", ((String[])entry.getValue())))
                    .append("&");
            }else if(entry.getValue() instanceof Collection){
                buffer.append(entry.getKey()).append(
                        CollUtil.join(((Collection<String>)entry.getValue()),",")
                ).append("&");
            }
        }
        return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 1) : StrUtil.EMPTY;
    }

    /**
     * 获取请求url中的uri
     *
     * @param url
     * @return
     */
    public static String getUri(String url){
        if(StringUtils.isEmpty(url)) {
            return null;
        }

        String uri = url;
        //uri中去掉 http:// 或者https
        if(uri.contains("http://") ){
            uri = uri.replace("http://", StrUtil.EMPTY);
        }else if(uri.contains("https://")){
            uri = uri.replace("https://", StrUtil.EMPTY);
        }

        int endIndex = uri.length(); //uri 在url中的最后一个字符的序号+1
        if(uri.contains("?")){
            endIndex = uri.indexOf("?");
        }
        return uri.substring(uri.indexOf("/"), endIndex);
    }

    public static String getRemoteAddr() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "";
        }
        return request.getRemoteAddr();
    }

    public static CookieBuilder cookieBuilder(){
        return new CookieBuilder(getRequest(), getResponse());
    }
}
