package com.GaoQue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class AppConfig2 implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Đảm bảo Spring Boot có thể phục vụ ảnh từ thư mục static/images/
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("classpath:/static/assets/img");
        }
    }
