package com.wuyou.mongo.repository;

import com.wuyou.mongo.WithVersion;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * * @author hjn
 * * @created 2019-01-09
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

    T findAndRemove(ID id, Class<T> domainClass);

    boolean addToSetByID(ID id, String propName, Object propElement, Class<T> domainClass);

    boolean deleteFromSetByID(ID id, String propName, Object propElement, Class<T> domainClass);

    boolean deleteFromSetByCondition(Map<String, Object> eqConditions, String propName, Object propElement, Class<T> domainClass, boolean multiUpdate);

    /**
     * 根据ID递增指定字段的值
     *
     * @param id
     * @param propName
     * @param incValue
     * @param domainClass
     * @return
     */
    T incFieldById(ID id, String propName, Number incValue, Class<T> domainClass);

    T incFieldById(ID id, String propName, Object incValue, Class<T> domainClass);

    /**
     * 根据ID和条件递增指定字段的值，保证递增后，属性值不小于零
     *
     * @param id
     * @param propName
     * @param incValue
     * @param domainClass
     * @return
     */
    T incFieldByIdAndEnoughValue(ID id, String propName, Number incValue, Class<T> domainClass);

    /**
     * 根据ID和集合名称递增指定字段的值，如果未找到条目则创建
     *
     * @param id
     * @param propName
     * @param incValue
     * @param domainClass
     * @param collectionName
     * @return
     */
    T incFieldByIdAndCollectionName(ID id, String propName, Number incValue, Class<T> domainClass, String collectionName);

    T incFieldByCondition(Map<String, Object> eqConditions, String propName, Number incValue, Class<T> domainClass) throws NoSuchFieldException, IllegalAccessException;

    boolean setFieldByCondition(String conditionPropName, Object conditionPropValue, String propName, Object propValue, Class<T> domainClass, boolean multiUpdate);

    boolean setFieldByID(ID id, String propName, Object propValue, Class<T> domainClass);


    boolean setMultiFieldsByID(ID id, List<String> propNames,
                               T valueBox) throws NoSuchFieldException, IllegalAccessException;

    <S extends WithVersion> boolean setMultiFieldsByIDAndVersion(ID id, long version, List<String> propNames,
                                                                 S valueBox) throws NoSuchFieldException, IllegalAccessException;

    boolean setMultiFieldByCondition(Map<String, Object> eqConditions, List<String> propNames, T valueBox, boolean multiUpdate) throws NoSuchFieldException, IllegalAccessException;

    List<T> pageQuery(Map<String, Object> eqConditions, Object primarySortOffset, Sort sort, int limit, int skip, Class<T> entityClass);

    GeoResults<T> findNear(Map<String, Object> eqConditions, Point coordinate, Sort sort, int limit, int skip, double maxDistance, double minDistance, Class<T> domainClass);
}
