package com.wuyou.databasetransfer.repository;

import com.wuyou.databasetransfer.entities.MongoService;
import com.wuyou.databasetransfer.entities.Service;
import com.wuyou.mongo.repository.BaseRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@Repository
public interface MongoServiceRepository extends BaseRepository<MongoService, ObjectId> {

}
