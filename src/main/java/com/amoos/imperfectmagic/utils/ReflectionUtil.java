package com.amoos.imperfectmagic.utils;

import com.amoos.imperfectmagic.ImperfectMagic;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
    public static final String ENTITY_DATA_HEALTH_ID = "ENTITY_DATA_HEALTH_ID";
    public static final String ENTITY_LEVEL = "ENTITY_LEVEL";
    public static final String ENTITY_ABSORPTION = "ENTITY_ABSORPTION";

    @Nullable
    public static <T> Object get(T object, String fieldName){
        fieldName = getFieldName(fieldName);
        int i = 0;
        List<Class<?>> classes = getSuperClass(object.getClass());
        for(Class<?> clazz : classes){
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object toReturn = field.get(object);
                if (toReturn != null) return toReturn;
            }catch (Exception e){
                if(!(e instanceof NoSuchFieldException) || ++i>=classes.size()) ImperfectMagic.LOGGER.info(String.valueOf(e));
            }
        }
        return null;
    }

    @Nullable
    public static <T> Object get(Class<T> tClass, String fieldName){
        fieldName = getFieldName(fieldName);
        try{
            return tClass.getDeclaredField(fieldName).get(null);
        } catch (Exception ignored){}
        return null;
    }
    public static <T> void set(Class<T> tClass, String fieldName, @Nullable T value){
        fieldName = getFieldName(fieldName);
        try{
            Class<?> fieldClass = tClass.getDeclaredField(fieldName).get(null).getClass();
            if(value==null||extendsFrom(value, fieldClass)){
                tClass.getDeclaredField(fieldName).set(null, value);
            }else ImperfectMagic.LOGGER.warn("value" + value + "isn't an instance of" + "t" + fieldClass + "!");

        } catch (Exception ignored){}
    }

    public static <T> void set(T object, String fieldName, @Nullable T value){
        fieldName = getFieldName(fieldName);
        int i = 0;
        List<Class<?>> classes = getSuperClass(object.getClass());
        for(Class<?> clazz : classes){
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                Class<?> fieldClass = field.get(object).getClass();
                if(value==null||extendsFrom(value, fieldClass)) field.set(object, value);
                else ImperfectMagic.LOGGER.warn("value" + value + "isn't an instance of" + "t" + fieldClass + "!");
            }catch (Exception e){if(!(e instanceof NoSuchFieldException) || ++i>=classes.size()) ImperfectMagic.LOGGER.info(String.valueOf(e));}
        }

        /*fieldName = getFieldName(fieldName);
        try{
            Field  field = object.getClass().getDeclaredField(fieldName);
            Class<?> fieldClass = field.get(object).getClass();
            if(value==null||extendsFrom(value, fieldClass)){
                field.set(object, value);
            }else ImperfectMagic.LOGGER.warn("value" + value + "isn't an instance of" + "t" + fieldClass + "!");

        } catch (Exception ignored){}*/
    }

    public static <T> boolean extendsFrom(Object obj, Class<T> tClass){
        return tClass.isAssignableFrom(obj.getClass());
    }
    public static String getFieldName(String field){
        switch (field) {
            case "ENTITY_DATA_HEALTH_ID" -> {
                return ImperfectMagic.isBeta ? "DATA_HEALTH_ID" : "f_20961_";
            }
            case "ENTITY_LEVEL" -> {
                return ImperfectMagic.isBeta ? "level" : "f_19853_";
            }
            case "ENTITY_ABSORPTION" -> {
                return ImperfectMagic.isBeta ? "absorptionAmount" : "f_20955_";
            }
        }
        ImperfectMagic.LOGGER.warn(field + "hasn't been recorded!");
        return field;
    }

    public static List<Class<?>> getSuperClass(Class<?> clazz){
        List<Class<?>> listSuperClass = new ArrayList<>();
        listSuperClass.add(clazz);
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            if(superclass.getName().equals("java.lang.Object")) {
                break;
            }
                listSuperClass.add(superclass);
                superclass = superclass.getSuperclass();
            }
        return listSuperClass;
    }
}
