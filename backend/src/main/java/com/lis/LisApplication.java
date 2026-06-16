package com.lis;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lis.mapper")
@Slf4j
public class LisApplication {
    public static void main(String[] args) {
        SpringApplication.run(LisApplication.class, args);
    }
}

