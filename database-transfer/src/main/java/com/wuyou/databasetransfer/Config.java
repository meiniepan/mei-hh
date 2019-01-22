package com.wuyou.databasetransfer;

import com.wuyou.mongo.repository.BaseRepositoryImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author hjn
 * @created 2019-01-09
 **/
@Configuration
@EnableMongoRepositories(repositoryBaseClass = BaseRepositoryImpl.class, basePackages = ("com.wuyou"))
@ComponentScan(basePackages = ("com.wuyou"))
public class Config extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }

}
