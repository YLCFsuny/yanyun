package com.hmall.user.config;

import com.hmall.common.config.BaseSecurityConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig  extends BaseSecurityConfig {

    private final JwtProperties properties;

    public SecurityConfig(JwtProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Override
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        properties.getLocation(),
                        properties.getPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair(
                properties.getAlias(),
                properties.getPassword().toCharArray());
    }

//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public KeyPair keyPair(JwtProperties properties){
//        // 获取秘钥工厂
//        KeyStoreKeyFactory keyStoreKeyFactory =
//                new KeyStoreKeyFactory(
//                        properties.getLocation(),
//                        properties.getPassword().toCharArray());
//        //读取钥匙对
//        return keyStoreKeyFactory.getKeyPair(
//                properties.getAlias(),
//                properties.getPassword().toCharArray());
//    }

}