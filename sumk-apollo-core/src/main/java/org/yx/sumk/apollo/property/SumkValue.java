package org.yx.sumk.apollo.property;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author : wjiajun
 * @description:
 */
public class SumkValue {

    private String key;

    /**
     * 是否 JSON
     */
    private boolean isJson;

    /**
     * 泛型。当是 JSON 类型时，使用
     */
    private Type genericType;

    private Method method;

    private Field field;

    /**
     * Bean 对象
     */
    private WeakReference<Object> beanRef;

    public SumkValue(String key, Object bean, Field field, boolean isJson) {
        this.beanRef = new WeakReference<>(bean);
        this.field = field;
        this.key = key;
        this.isJson = isJson;
        if (isJson) {
            this.genericType = field.getGenericType();
        }
    }

    public SumkValue(String key, Object bean, Method method, boolean isJson) {
        this.beanRef = new WeakReference<>(bean);
        this.method = method;
        this.key = key;
        this.isJson = isJson;
        if (isJson) {
            this.genericType = method.getGenericParameterTypes()[0];
        }
    }

    public void update(Object newVal) throws IllegalAccessException, InvocationTargetException {
        if (isField()) {
            injectField(newVal);
        } else {
            injectMethod(newVal);
        }
    }

    private void injectField(Object newVal) throws IllegalAccessException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(bean, newVal);
        field.setAccessible(accessible);
    }

    private void injectMethod(Object newVal)
            throws InvocationTargetException, IllegalAccessException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }
        method.invoke(bean, newVal);
    }

    public boolean isJson() {
        return isJson;
    }

    public Type getGenericType() {
        return genericType;
    }

    public boolean isField() {
        return this.field != null;
    }

    boolean isTargetBeanValid() {
        return beanRef.get() != null;
    }
}
