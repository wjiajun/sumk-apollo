package org.yx.sumk.apollo.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.ConfigConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yx.conf.AbstractRefreshableSystemConfig;
import org.yx.conf.AppInfo;
import org.yx.util.StringUtil;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : wjiajun
 * Apollo系统配置
 */
public class ApolloSystemConfig extends AbstractRefreshableSystemConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApolloSystemConfig.class);

    private volatile List<ApolloPropertyConfig> configs = new LinkedList<>();

    @Override
    protected void init() {
        boolean enableApollo = AppInfo.getBoolean("apollo.bootstrap.enabled", true);
        if (!enableApollo) {
            throw new IllegalArgumentException("apollo.bootstrap.enabled parameter must be on");
        }

        // apollo 相关配置初始化
        String namespaces = AppInfo.get("apollo.bootstrap.namespaces", ConfigConsts.NAMESPACE_APPLICATION);
        logger.debug("use apollo namespaces: {}", namespaces);

        StringUtil.splitAndTrim(StringUtil.toLatin(namespaces), ",").forEach(namespace -> {
            // registry namespace config
            Config config = ConfigService.getConfig(namespace);
            ApolloPropertyConfigFactory.registry(namespace, config);
        });

        configs = ApolloPropertyConfigFactory.getApolloPropertyConfigs();
    }

    @Override
    public Map<String, String> values() {
        Map<String, String> configValues = new LinkedHashMap<>();
        for (ApolloPropertyConfig config : configs) {
            configValues.putAll(config.values());
        }

        return configValues;
    }

    @Override
    public String get(String s) {
        for (ApolloPropertyConfig config : configs) {
            String value = config.get(s);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Set<String> keys() {
        Set<String> names = new LinkedHashSet<>();
        for (ApolloPropertyConfig config : configs) {
            names.addAll(config.keys());
        }
        return names;
    }

    @Override
    public void stop() {
        // noting
    }
}
