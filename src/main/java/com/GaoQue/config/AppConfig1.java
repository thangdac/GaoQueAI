package com.GaoQue.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig1 {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
