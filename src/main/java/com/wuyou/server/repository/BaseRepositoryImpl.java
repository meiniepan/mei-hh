package com.wuyou.server.repository;

import com.mongodb.WriteResult;
import com.wuyou.server.entities.WithId;
import com.wuyou.server.entities.WithVersion;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * * @author hjn
 * * @created 2019-01-09
 */
public class BaseRepositoryImpl<T, ID extends Serializable>
        extends SimpleMongoRepository<T, ID> implements BaseRepository<T, ID> {

    private MongoOperations mongoOperations;

    public BaseRepositoryImpl(MongoEntityInformation mongoEntityInformation, MongoOperations mongoOperations) {
        super(mongoEntityInformation, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    public T findAndRemove(ID id, Class<T> domainClass) {
        return mongoOperations.findAndRemove(new Query(where(WithId.NAME).is(id)), domainClass);
    }

    @Override
    public boolean addToSetByID(ID id, String propName, Object propElement, Class<T> domainClass) {
        return updateOneByID(id, new Update().addToSet(propName, propElement), domainClass);
    }

    @Override
    public boolean deleteFromSetByID(ID id, String propName, Object propElement, Class<T> domainClass) {
        return updateOneByID(id, new Update().pull(propName, propElement), domainClass);
    }

    @Override
    public boolean deleteFromSetByCondition(Map<String, Object> eqConditions, String propName, Object propElement, Class<T> domainClass, boolean multiUpdate) {
        return updateByECondition(eqConditions, new Update().pull(propName, propElement), domainClass, multiUpdate);
    }

    @Override
    public T incFieldById(ID id, String propName, Number incValue, Class<T> domainClass) {
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(false);
        return mongoOperations.<T>findAndModify(new Query(where(WithId.NAME).is(id)), new Update().inc(propName, incValue), options, domainClass);
    }

    @Override
    public T incFieldById(ID id, String propName, Object incValue, Class<T> domainClass) {
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(false);
        return mongoOperations.<T>findAndModify(new Query(where(WithId.NAME).is(id)), new Update().push(propName, incValue), options, domainClass);
    }

    @Override
    public T incFieldByIdAndEnoughValue(ID id, String propName, Number incValue, Class<T> domainClass) {
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(false);
        Criteria criteria = where(WithId.NAME).is(id);

        double doubleValue = incValue.doubleValue();
        if (doubleValue < 0) {
            criteria = criteria.and(propName).gte(Math.abs(doubleValue));
        }

        return mongoOperations.<T>findAndModify(new Query(criteria), new Update().inc(propName, incValue), options, domainClass);
    }

    @Override
    public T incFieldByIdAndCollectionName(ID id, String propName, Number incValue, Class<T> domainClass, String collectionName) {
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(false).upsert(true);
        return mongoOperations.<T>findAndModify(new Query(where(WithId.NAME).is(id)), new Update().inc(propName, incValue), options, domainClass, collectionName);
    }

    @Override
    public T incFieldByCondition(Map<String, Object> eqConditions, String propName, Number incValue, Class<T> domainClass) throws NoSuchFieldException, IllegalAccessException {
        Criteria criteria = convertEConditions2Criteria(eqConditions);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(false).upsert(true);
        return mongoOperations.<T>findAndModify(new Query(criteria), new Update().inc(propName, incValue), options, domainClass);
    }

    @Override
    public boolean setFieldByID(ID id, String propName, Object propValue, Class<T> domainClass) {
        return updateOneByID(id, Update.update(propName, propValue), domainClass);
    }

    @Override
    public boolean setFieldByCondition(String conditionPropName, Object conditionPropValue, String propName, Object propValue, Class<T> domainClass, boolean multiUpdate) {
        Map<String, Object> epConditions = new HashMap<>();
        epConditions.put(conditionPropName, conditionPropValue);
        return updateByECondition(epConditions, Update.update(propName, propValue), domainClass, multiUpdate);
    }

    @Override
    public boolean setMultiFieldsByID(ID id, List<String> propNames,
                                      T valueBox) throws NoSuchFieldException, IllegalAccessException {
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

    @Override
    public <S extends WithVersion> boolean setMultiFieldsByIDAndVersion(ID id, long version, List<String> propNames, S valueBox) throws NoSuchFieldException, IllegalAccessException {
        if (propNames.size() == 0) return false;

        Class domainClass = valueBox.getClass();
        Update update = null;

        for (String propName : propNames) {
            if (WithVersion.NAME.equals(propName)) {
                continue;
            }
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

        return updateOneByIDAndVersion(id, version, update.inc(WithVersion.NAME, 1), domainClass);
    }


    @Override
    public boolean setMultiFieldByCondition(Map<String, Object> eqConditions, List<String> propNames, T valueBox, boolean multiUpdate)
            throws NoSuchFieldException, IllegalAccessException {
        if (propNames.size() == 0) return false;

        Update update = null;
        Class domainClass = valueBox.getClass();
        for (String propName : propNames) {
            if (WithVersion.NAME.equals(propName)) {
                continue;
            }
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

        return updateByECondition(eqConditions, update, domainClass, multiUpdate);
    }

    public List<T> pageQuery(Map<String, Object> eqConditions, Object primarySortOffset, Sort sort, int limit, int skip,
                             Class<T> entityClass) {
        Criteria criteria = eqConditions != null ? convertEConditions2Criteria(eqConditions) : null;

        if (primarySortOffset != null) {
            Iterator<Sort.Order> iterator = sort.iterator();
            if (iterator.hasNext()) {
                Sort.Order order = iterator.next();

                if (criteria != null) {
                    criteria = criteria.and(order.getProperty());
                } else {
                    criteria = Criteria.where(order.getProperty());
                }

                switch (order.getDirection()) {
                    case DESC:
                        criteria = criteria.lte(primarySortOffset);
                        break;
                    default:
                        criteria = criteria.gte(primarySortOffset);
                        break;
                }
            }
        }

        Query query = new Query().with(sort).skip(skip).limit(limit);
        if (criteria != null) {
            query.addCriteria(criteria);
        }

        return mongoOperations.find(query, entityClass);
    }

    private boolean updateOneByID(ID id, Update update, Class<T> domainClass) {
        WriteResult writeResult = mongoOperations.updateFirst(
                query(where(WithId.NAME).is(id)), update, domainClass);
        return writeResult.getN() > 0;
    }

    private boolean updateOneByIDAndVersion(ID id, long version, Update update, Class<T> domainClass) {
        WriteResult writeResult = mongoOperations.updateFirst(
                query(where(WithId.NAME).is(id).and(WithVersion.NAME).is(version)), update, domainClass);
        return writeResult.getN() > 0;
    }

    private boolean updateByECondition(Map<String, Object> eqConditions, Update update, Class<T> domainClass, boolean multiUpdates) {
        Criteria criteria = convertEConditions2Criteria(eqConditions);
        WriteResult writeResult = null;
        if (multiUpdates) {
            writeResult = mongoOperations.updateMulti(query(criteria), update, domainClass);
        } else {
            writeResult = mongoOperations.updateFirst(query(criteria), update, domainClass);
        }
        return writeResult.getN() > 0;
    }

    private Criteria convertEConditions2Criteria(Map<String, Object> eqConditions) {
        Criteria criteria = null;
        for (Map.Entry<String, Object> entity : eqConditions.entrySet()) {
            String key = entity.getKey();
            Object value = entity.getValue();

            if (!"coordinate".equals(key)) {
                if (criteria != null) {
                    criteria = criteria.and(key);
                } else {
                    criteria = Criteria.where(key);
                }
            }

            if (value instanceof Collection) {
                criteria = criteria.in((Collection) value);
            } else {
                criteria = criteria.is(value);
            }

        }
        return criteria;
    }

    public GeoResults<T> findNear(Map<String, Object> eqConditions, Point coordinate, Sort sort, int limit, int skip, double maxDistance, double minDistance, Class<T> domainClass) {
        Criteria criteria = eqConditions != null ? convertEConditions2Criteria(eqConditions) : null;
        NearQuery query = NearQuery.near(coordinate).minDistance(new Distance(minDistance, Metrics.KILOMETERS)).maxDistance(new Distance(maxDistance, Metrics.MILES)).query(new Query(criteria).limit(limit).skip(skip));

        GeoResults<T> geoResults = mongoOperations.geoNear(query, domainClass);
        return geoResults;
    }
}
