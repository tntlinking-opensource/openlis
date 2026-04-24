package com.lis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lis.mapper")
public class LisApplication {
    public static void main(String[] args) {
        SpringApplication.run(LisApplication.class, args);
    }
}

