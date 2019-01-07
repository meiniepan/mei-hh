package com.wuyou.server.customer;


import com.mongodb.WriteResult;
import com.wuyou.server.entities.Customer;
import com.wuyou.server.entities.WithId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


public class CustomerRepositoryImpl {
    @Autowired
    MongoTemplate mongoTemplate;

    public boolean setMultiFieldsByID(ObjectId id, List<String> propNames,
                               Customer valueBox) throws NoSuchFieldException, IllegalAccessException {
        if (propNames.size() == 0) return false;

        Class domainClass = valueBox.getClass();
        Update update = null;

        for (String propName : propNames) {
            Field field = domainClass.getDeclaredField(propName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Object propValue = field.get(valueBox);

            // TODO 值校验

            if (update == null) {
                update = Update.update(propName, propValue);
            } else {
                update = update.set(propName, propValue);
            }
        }
        return updateOneByID(id, update, domainClass);
    }

    public boolean updateOneByID(ObjectId id, Update update, Class<Customer> domainClass) {
        WriteResult writeResult = mongoTemplate.updateFirst(
                query(where(WithId.NAME).is(id)), update, domainClass);
        return writeResult.getN() > 0;
    }

}
