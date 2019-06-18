/*
 * @Date @Time.
 * @Author kinwyb<kinwyb@aliyun.com>
 */
package com.heldiam.jrpcx.core.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 返回结果池
 *
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public class FeaturePool {

    private static final Integer MAX = 1000;

    private static final LinkedList<Feature> POOL = new LinkedList<>();

    public static synchronized Feature get() {
        if (POOL.size() > 0) {
            return POOL.pop();
        }
        return new Feature();
    }

    public static synchronized void push(Feature feature) {
        if (POOL.size() < MAX) {
            POOL.push(feature);
        }
    }

    /**
     * 对象相同属性copy
     *
     * @param obj
     * @param t
     */
    public static void cloneObj(Object obj, Object t) {
        if (obj == null || t == null) {
            return;
        }
        try {
            Class srcClass = obj.getClass();
            Field[] fields = srcClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);//修改访问权限
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.set(t, field.get(obj)); //强制设置对象
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private final static Map<String, Feature> useFeatureMap = new HashMap<>();

    public static void AddUseFeature(String id, Feature f) {
        useFeatureMap.put(id, f);
    }

    public static Feature GetUseFeature(String id) {
        return useFeatureMap.remove(id);
    }

}
