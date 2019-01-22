package com.wuyou.databasetransfer.repository;

import com.wuyou.databasetransfer.entities.MongoShop;
import com.wuyou.databasetransfer.entities.Shop;
import com.wuyou.mongo.repository.BaseRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@Repository
public interface MongoShopRepository extends BaseRepository<MongoShop, ObjectId> {

}
