package test;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInfoInterceptorPro implements HandlerInterceptor {

    private static final String USER_INFO_HEADER = "user-info";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        String userInfo = request.getHeader(USER_INFO_HEADER);

        if (StrUtil.isNotBlank(userInfo)) {  // 检查是否为空或空白字符串
            try {
                Long userId = Long.valueOf(userInfo);
                // 添加用户信息验证
                if (!isValidUserId(userId)) {
                    log.warn("无效的用户ID: {}", userId);
                    return true; // 不中断流程但记录警告
                }
                UserContext.setUser(userId);
                log.debug("用户信息已设置到ThreadLocal: userId={}", userId);
            } catch (NumberFormatException e) {
                log.warn("用户信息格式错误: {}", userInfo); // 可以记录但不中断流程
            }
        } else {
            log.debug("请求头中未找到用户信息: {}", request.getRequestURI());
        }

        request.setAttribute("interceptorStartTime", startTime);
        return true;
    }

    private boolean isValidUserId(Long userId) {
        return userId != null && userId > 0;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        try {
            // 记录执行时间
            Long startTime = (Long) request.getAttribute("interceptorStartTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                log.debug("拦截器执行完成: uri={}, duration={}ms",
                        request.getRequestURI(), duration);
            }
            // 清理用户信息
            Long userId = UserContext.getUser();
            if (userId != null) {
                log.debug("清理用户信息: userId={}", userId);
            }
            UserContext.clear();
        } catch (Exception e) {
            log.error("拦截器afterCompletion执行异常", e);
            // 确保无论如何都清理ThreadLocal
            UserContext.clear();
        }
    }
}