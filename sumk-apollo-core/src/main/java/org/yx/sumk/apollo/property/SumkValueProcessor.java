package org.yx.sumk.apollo.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yx.conf.AppInfo;
import org.yx.sumk.apollo.annotation.Value;
import org.yx.util.CollectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author : wjiajun
 * @description: sumk value 相关注解处理
 */
public class SumkValueProcessor extends ApolloConfigProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SumkValueProcessor.class);

    public SumkValueProcessor() {
    }

    @Override
    protected void processField(Object bean, Field field) {
        // register @Value on field
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        Set<String> keys = placeholderUtil.extractPlaceholderKeys(value.value());

        if (keys.isEmpty()) {
            return;
        }

        String propertyValue = keys.stream().findFirst().orElse(null);
        if (CollectionUtil.isNotEmpty(keys) && keys.size() > 1) {
            logger.warn("Value 配置了多个表达式，选择{} 作为最终结果", propertyValue);
        }

        // 调用 Method ，设置值
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(bean, AppInfo.get(propertyValue));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "reflection exception - ", e);
        }
        field.setAccessible(accessible);

        for (String key : keys) {
            SumkValue sumkValue = new SumkValue(key, bean, field, false);
            sumkValueRegistry.register(key, sumkValue);
            logger.debug("Monitoring {}", sumkValue);
        }
    }

    @Override
    protected void processMethod(Object bean, Method method) {
        //register @Value on method
        Value value = method.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        //skip Configuration bean methods
        // 忽略非 setting 方法
        if (method.getParameterTypes().length != 1) {
            logger.error("Ignore @Value setter {}.{}, expecting 1 parameter, actual {} parameters",
                    bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
            return;
        }

        Set<String> keys = placeholderUtil.extractPlaceholderKeys(value.value());
        if (keys.isEmpty()) {
            return;
        }

        String propertyValue = keys.stream().findFirst().orElse(null);
        if (CollectionUtil.isNotEmpty(keys) && keys.size() > 1) {
            logger.warn("Value 配置了多个表达式，选择{} 作为最终结果", propertyValue);
        }

        // 调用 Method ，设置值
        boolean accessible = method.isAccessible();
        method.setAccessible(true);
        try {
            method.invoke(bean, AppInfo.get(propertyValue));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "reflection exception - ", e);
        }
        method.setAccessible(accessible);

        for (String key : keys) {
            SumkValue sumkValue = new SumkValue(key, bean, method, false);
            sumkValueRegistry.register(key, sumkValue);
            logger.info("Monitoring {}", sumkValue);
        }
    }
}
