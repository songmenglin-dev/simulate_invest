package com.stock.analysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.stock.analysis.mapper")
public class AnalysisServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalysisServiceApplication.class, args);
    }
}