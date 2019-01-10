package com.wuyou.base.util;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class UUIDUtils {

    public static boolean isValidUUID(String uuid) {
        return uuid != null && ObjectId.isValid(uuid);
    }

    public static String generateUUID() {
        return generateObjectId().toHexString();
    }

    public static String generateUUID(ObjectId objectId) {
        return objectId != null ? objectId.toHexString() : null;
    }

    public static ObjectId generateObjectId() {
        return ObjectId.get();
    }

    public static ObjectId generateObjectId(String uuid) {
        return isValidUUID(uuid) ? new ObjectId(uuid) : null;
    }

    public static List<ObjectId> generateObjectIdList(List<String> uuidList) {
        List<ObjectId> objectIdList = new ArrayList<>();
        for (String uuid : uuidList){
            if (isValidUUID(uuid)){
                objectIdList.add(new ObjectId(uuid));
            }
        }
        return objectIdList;
    }

    public static List<String> generateUUIDList(List<ObjectId> objectIdList) {
        List<String> uuidList = new ArrayList<>();
        for (ObjectId objectId  : objectIdList){
            if (objectId != null){
                uuidList.add(objectId.toHexString());
            }
        }
        return uuidList;
    }

    public static Set<ObjectId> generateObjectIdSet(Set<String> uuidSet){
        Set<ObjectId> objectIdSet = new HashSet<>();
        for (String uuid : uuidSet){
            if (isValidUUID(uuid)){
                objectIdSet.add(new ObjectId(uuid));
            }
        }
        return objectIdSet;
    }

    public static Set<String> generateUUIDSet(Set<ObjectId> objectIdSet){
        Set<String> uuidSet = new HashSet<>();
        for (ObjectId objectId : objectIdSet){
            if (objectId != null){
                uuidSet.add(objectId.toHexString());
            }
        }
        return uuidSet;
    }
}
