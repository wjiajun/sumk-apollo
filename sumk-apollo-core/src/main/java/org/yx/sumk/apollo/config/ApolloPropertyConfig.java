package org.yx.sumk.apollo.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import org.yx.conf.AbstractRefreshableSystemConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : wjiajun
 *  Apollo属性配置，ApolloPropertyConfig : Namespace = 1 : 1
 */
public class ApolloPropertyConfig extends AbstractRefreshableSystemConfig {

    private static final Map<String, String> EMPTY_MAP = new HashMap<>(0);

    private final Config source;

    public ApolloPropertyConfig(Config source) {
        this.source = source;
    }

    @Override
    protected void init() {
    }

    @Override
    public Map<String, String> values() {
        Set<String> propertyNames = source.getPropertyNames();
        if (propertyNames.isEmpty()) {
            return EMPTY_MAP;
        }
        Map<String, String> map = new LinkedHashMap<>((int) ((float) propertyNames.size() / 0.75F + 1.0F));
        for (String propertyName : propertyNames) {
            map.put(propertyName, source.getProperty(propertyName, null));
        }
        return map;
    }

    @Override
    public String get(String s) {
        return source.getProperty(s, null);
    }

    @Override
    public Set<String> keys() {
        return source.getPropertyNames();
    }

    @Override
    public void stop() {
        // noting
    }

    public void addListener(ConfigChangeListener listener) {
        source.addChangeListener(listener);
    }
}
