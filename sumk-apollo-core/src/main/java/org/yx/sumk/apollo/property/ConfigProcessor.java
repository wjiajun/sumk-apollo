package org.yx.sumk.apollo.property;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.yx.sumk.apollo.util.PlaceholderUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author : wjiajun
 */
public abstract class ConfigProcessor {

    protected final SumkValueRegistry sumkValueRegistry;
    protected final PlaceholderUtil placeholderUtil;

    public ConfigProcessor() {
        sumkValueRegistry = new SumkValueRegistry();
        placeholderUtil = new PlaceholderUtil();
    }

    public void processBean(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Field field : FieldUtils.getAllFields(clazz)) {
            processField(bean, field);
        }
        // 处理所有的 Method
        Method[] methods = AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return clazz.getMethods();
            }
        });
        for (Method method : methods) {
            processMethod(bean, method);
        }
    }

    protected abstract void processField(Object bean, Field field);

    protected abstract void processMethod(Object bean, Method method);
}
