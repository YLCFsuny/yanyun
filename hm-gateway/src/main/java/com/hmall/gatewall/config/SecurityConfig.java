package com.hmall.gatewall.config;

import com.hmall.common.config.BaseSecurityConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
//@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    // 密码编码器，用于对密码进行加密和解密
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();  // 使用BCrypt算法进行加密
    }

    @Bean
    // 密钥对，用于生成和验证JWT的签名
    // 从配置文件中读取密钥对的位置、密码和别名
    // 并使用KeyStoreKeyFactory读取密钥对
    public KeyPair keyPair(JwtProperties properties){
        // 获取秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        properties.getLocation(),
                        properties.getPassword().toCharArray());
        // 读取钥匙对
        return keyStoreKeyFactory.getKeyPair(
                properties.getAlias(),
                properties.getPassword().toCharArray());
    }

//    public class SecurityConfig extends BaseSecurityConfig {
//    private final JwtProperties properties;
//
//    public SecurityConfig(JwtProperties properties) {
//        this.properties = properties;
//    }
//
//    @Bean
//    @Override
//    public KeyPair keyPair() {
//        KeyStoreKeyFactory keyStoreKeyFactory =
//                new KeyStoreKeyFactory(
//                        properties.getLocation(),
//                        properties.getPassword().toCharArray());
//        return keyStoreKeyFactory.getKeyPair(
//                properties.getAlias(),
//                properties.getPassword().toCharArray());
//    }

}
