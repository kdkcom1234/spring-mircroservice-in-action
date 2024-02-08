package com.optimagrowth.license.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
// 구성값 읽어오기
@ConfigurationProperties(prefix = "example")
@Getter @Setter
public class ServiceConfig{

    private String property;

}