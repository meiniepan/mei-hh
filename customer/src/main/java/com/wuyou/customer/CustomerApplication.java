package com.wuyou.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class CustomerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}

