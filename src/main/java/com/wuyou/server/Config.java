package com.wuyou.server;

import com.wuyou.server.repository.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author hjn
 * @created 2019-01-09
 **/
@Configuration
@EnableMongoRepositories(repositoryBaseClass = BaseRepositoryImpl.class, basePackages = ("com.wuyou"))
public class Config {
}
