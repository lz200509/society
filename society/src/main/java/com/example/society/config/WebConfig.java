package com.example.society.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 针对所有API路径配置CORS
                registry.addMapping("/api/**")
                        // 使用 allowedOriginPatterns 替代 allowedOrigins
                        .allowedOriginPatterns("http://localhost:5173", "http://127.0.0.1:5173")
                        // 允许的HTTP方法
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        // 允许的请求头
                        .allowedHeaders("*")
                        // 是否允许凭证（cookies等）
                        .allowCredentials(true)
                        // 预检请求缓存时间（秒）
                        .maxAge(3600);

                // 如果需要，可以为其他路径也配置CORS
                registry.addMapping("/public/**")
                        .allowedOriginPatterns("*")  // 这里也可以用通配符
                        .allowedMethods("GET", "POST")
                        .allowCredentials(false)     // 公开接口不需要凭证
                        .maxAge(3600);
            }
        };
    }
}