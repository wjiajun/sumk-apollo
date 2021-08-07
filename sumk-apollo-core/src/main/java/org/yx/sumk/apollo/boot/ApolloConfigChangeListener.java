package org.yx.sumk.apollo.boot;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yx.conf.AppInfo;
import org.yx.sumk.apollo.property.SumkValue;
import org.yx.sumk.apollo.property.SumkValueRegistry;
import org.yx.util.CollectionUtil;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * @author : wjiajun
 */
public class ApolloConfigChangeListener implements ConfigChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(ApolloConfigChangeListener.class);

    private final Gson gson;

    public ApolloConfigChangeListener() {
        this.gson = new Gson();
        registryObserver();
    }

    @Override
    public void onChange(ConfigChangeEvent configChangeEvent) {
        logger.info("apollo remote sync change:{}", configChangeEvent);

        Set<String> keys = configChangeEvent.changedKeys();
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }

        // refresh all listener
        ApolloWatcher.notifyUpdate();
    }

    private void registryObserver() {
        AppInfo.addObserver((info) -> {
            for (String key : info.keys()) {
                Collection<SumkValue> sumkValues = SumkValueRegistry.get(key);
                if (sumkValues == null || sumkValues.isEmpty()) {
                    continue;
                }

                // 是否更新
                SumkValue sumkValue = sumkValues.stream().findFirst().get();
                if(Objects.equals(sumkValue.curValue(), info.get(key))) {
                    continue;
                }

                // 循环，更新 value
                for (SumkValue val : sumkValues) {
                    updateValue(val, key);
                }
            }
        });
    }

    private void updateValue(SumkValue val, String key) {
        try {
            Object value = resolvePropertyValue(val, key);
            // 更新 StringValue
            val.update(value);

            logger.info("Auto update apollo changed value successfully, new value: {}, {}", value, val);
        } catch (Throwable ex) {
            logger.error("Auto update apollo changed value failed, {}", val.toString(), ex);
        }
    }

    private Object resolvePropertyValue(SumkValue val, String key) {
        Object str = AppInfo.get(key, null);
        if (val.isJson()) {
            // 如果值数据结构是 JSON 类型，则使用 Gson 解析成对应值的类型
            str = parseJsonValue((String) str, val.getGenericType());
        }
        val.refreshCurValue(str);
        return str;
    }

    private Object parseJsonValue(String json, Type targetType) {
        try {
            return gson.fromJson(json, targetType);
        } catch (Throwable ex) {
            logger.error("Parsing json '{}' to type {} failed!", json, targetType, ex);
            throw ex;
        }
    }
}
