package com.hx.webmagicdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebMagicdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebMagicdemoApplication.class, args);
    }

}
