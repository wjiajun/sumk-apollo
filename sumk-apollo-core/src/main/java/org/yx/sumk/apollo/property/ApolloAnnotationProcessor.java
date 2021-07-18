package org.yx.sumk.apollo.property;

import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yx.conf.AppInfo;
import org.yx.util.CollectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author : wjiajun
 * apollo 相关注解处理
 */
public class ApolloAnnotationProcessor extends ApolloConfigProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApolloAnnotationProcessor.class);

    private static final Gson GSON = new Gson();

    @Override
    protected void processField(Object bean, Field field) {
        this.processApolloJsonValue(bean, field);
    }

    @Override
    protected void processMethod(Object bean, Method method) {
        this.processApolloJsonValue(bean, method);
    }

    private void processApolloJsonValue(Object bean, Field field) {
        ApolloJsonValue apolloJsonValue = field.getAnnotation(ApolloJsonValue.class);
        if (apolloJsonValue == null) {
            return;
        }
        String placeholder = apolloJsonValue.value();
        Set<String> strings = placeholderUtil.extractPlaceholderKeys(placeholder);
        String propertyValue = strings.stream().findFirst().orElse(null);
        if (CollectionUtil.isNotEmpty(strings) && strings.size() > 1) {
            logger.warn("ApolloJsonValue 配置了多个表达式，选择{} 作为最终结果", propertyValue);
        }

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(bean, parseJsonValue(AppInfo.get(propertyValue), field.getGenericType()));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "reflection exception - ", e);
        }
        field.setAccessible(accessible);

        // 开启自动更新机制
        Set<String> keys = placeholderUtil.extractPlaceholderKeys(placeholder);
        for (String key : keys) {
            SumkValue sumkValue = new SumkValue(key, bean, field, true);
            sumkValueRegistry.register(key, sumkValue);
            logger.debug("Monitoring {}", sumkValue);
        }
    }

    private void processApolloJsonValue(Object bean, Method method) {
        ApolloJsonValue apolloJsonValue = method.getAnnotation(ApolloJsonValue.class);
        if (apolloJsonValue == null) {
            return;
        }
        String placeholder = apolloJsonValue.value();
        Set<String> strings = placeholderUtil.extractPlaceholderKeys(placeholder);
        String propertyValue = strings.stream().findFirst().orElse(null);
        if (CollectionUtil.isNotEmpty(strings) && strings.size() > 1) {
            logger.warn("ApolloJsonValue 配置了多个表达式，选择{} 作为最终结果", propertyValue);
        }

        Type[] types = method.getGenericParameterTypes();
        Preconditions.checkArgument(types.length == 1,
                "Ignore @Value setter {}.{}, expecting 1 parameter, actual {} parameters",
                bean.getClass().getName(), method.getName(), method.getParameterTypes().length);

        // 调用 Method ，设置值
        boolean accessible = method.isAccessible();
        method.setAccessible(true);
        try {
            method.invoke(bean, parseJsonValue(AppInfo.get(propertyValue), types[0]));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "reflection exception - ", e);
        }
        method.setAccessible(accessible);

        // 开启自动更新
        Set<String> keys = placeholderUtil.extractPlaceholderKeys(placeholder);
        for (String key : keys) {
            SumkValue sumkValue = new SumkValue(key, bean, method, true);
            sumkValueRegistry.register(key, sumkValue);
            logger.debug("Monitoring {}", sumkValue);
        }
    }

    private Object parseJsonValue(String json, Type targetType) {
        try {
            return GSON.fromJson(json, targetType);
        } catch (Throwable ex) {
            logger.error("Parsing json '{}' to type {} failed!", json, targetType, ex);
            throw ex;
        }
    }
}
