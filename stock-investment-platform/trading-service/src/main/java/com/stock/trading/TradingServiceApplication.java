package com.stock.trading;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.stock.trading.mapper")
public class TradingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingServiceApplication.class, args);
    }
}