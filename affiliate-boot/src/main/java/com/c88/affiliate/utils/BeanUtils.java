package com.c88.affiliate.utils;

import com.c88.affiliate.annotation.ForUpdate;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

    public static <T> Map<String, Object> getChangedFields(T newBean, T oldBean) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = newBean.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ForUpdate.class)) {
                try {
                    Object newValue = field.get(newBean);
                    Object oldValue = field.get(oldBean);
                    if (newValue != null && !newValue.equals(oldValue)) {
                        map.put(field.getAnnotation(ForUpdate.class).fieldName(), newValue);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        return map;
    }
}
