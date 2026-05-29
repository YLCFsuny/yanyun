package com.hmall.common.utils;

public class UserContext {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();
    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userId 用户id
     */
    public static void setUser(Long userId) {
        THREAD_LOCAL.set(userId);
    }
    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static Long getUser() {
        return THREAD_LOCAL.get();
    }
    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        THREAD_LOCAL.remove();
    }

    // 在现有UserContext类中添加
    public static void clear() {
        try {
            THREAD_LOCAL.remove();
        } catch (Exception e) {
            // 确保在任何情况下都能清理
            THREAD_LOCAL.set(null);
            THREAD_LOCAL.remove();
        }
    }
}
