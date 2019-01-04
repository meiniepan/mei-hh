package com.wuyou.server.util;

import org.bson.types.ObjectId;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhaoao.zhang on 15/11/2016.
 */
public class BeanUtils {

    public static <T> T copyProperties(Object source, T target) {
//        org.springframework.beans.BeanUtils.copyProperties(source, target);
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                PropertyDescriptor sourcePd = org.springframework.beans.BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null) {
                        Class<?> parameterType = writeMethod.getParameterTypes()[0];
                        Class<?> returnType = readMethod.getReturnType();

                        boolean isAssignable = ClassUtils.isAssignable(parameterType, returnType);
                        boolean objectId2String = String.class == parameterType && ObjectId.class == returnType;
                        boolean string2ObjectId = ObjectId.class == parameterType && String.class == returnType;

                        if (isAssignable || objectId2String || string2ObjectId) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }
                                Object value = readMethod.invoke(source);
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }

                                if (! isAssignable && value != null) {
                                    if (string2ObjectId) {
                                        value = new ObjectId(value.toString());
                                    } else if (objectId2String) {
                                        value = value.toString();
                                    }
                                }
                                writeMethod.invoke(target, value);
                            }
                            catch (Throwable ex) {
                                throw new FatalBeanException(
                                        "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                            }
                        }
                    }
                }
            }
        }
        return target;
    }

    public static String formatFieldValue(Object value, Class<?> fieldClass) {
        if (fieldClass == Date.class) {
            return String.valueOf(((Date)value).getTime());
        } else {
            return value.toString();
        }
    }

    public static Object convertFieldValue(String valueText, Class<?> fieldClass) throws NumberFormatException {
        if (fieldClass == Date.class) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(valueText));
            return calendar.getTime();
        } else if (fieldClass == Double.class || fieldClass == double.class) {
            return Double.valueOf(valueText);
        } else if (fieldClass == Float.class || fieldClass == float.class) {
            return Float.valueOf(valueText);
        } else if (fieldClass == Long.class || fieldClass == long.class) {
            return Long.valueOf(valueText);
        } else if (fieldClass == Integer.class || fieldClass == int.class) {
            return Integer.valueOf(valueText);
        } else if (fieldClass == BigDecimal.class) {
            return new BigDecimal(valueText);
        }

        return null;
    }

    public static Field getField(Class<?> objectClass, String fieldName) {
        try {
            return objectClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static boolean checkFieldCanBeSortedInsideStorage(Field field, Class<? extends Annotation> indexedAnnotationClass) {
        if (field != null) {
            Class<?> fieldType = field.getType();
            if (fieldType.equals(int.class) || fieldType.equals(long.class)
                    || fieldType.equals(float.class) || fieldType.equals(double.class)
                    || fieldType.equals(BigDecimal.class)
                    || fieldType.equals(Integer.class) || fieldType.equals(Long.class)
                    || fieldType.equals(Float.class) || fieldType.equals(Double.class)
                    || fieldType.equals(Date.class)) {
                return field.isAnnotationPresent(indexedAnnotationClass);
            }
        }
        return false;
    }

    public static boolean checkFieldCanBeQueryedInsideStorage(Field field, Class<? extends Annotation> indexedAnnotationClass) {
        return field != null && field.isAnnotationPresent(indexedAnnotationClass);
    }
}
