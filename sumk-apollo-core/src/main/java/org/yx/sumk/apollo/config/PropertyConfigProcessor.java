package org.yx.sumk.apollo.config;

import org.yx.conf.AppInfo;
import org.yx.sumk.apollo.boot.ApolloConfigChangeListener;

import java.util.List;

/**
 * @author : wjiajun
 * @description:
 */
public class PropertyConfigProcessor {

    /**
     * 配置动态刷新初始化
     */
    public void initializeAutoUpdate() {
        boolean enableApollo = AppInfo.getBoolean("apollo.bootstrap.enabled", true);
        if (!enableApollo) {
            return;
        }

        ApolloConfigChangeListener configChangeListener = new ApolloConfigChangeListener();
        List<ApolloPropertyConfig> apolloPropertyConfigs = ApolloPropertyConfigFactory.getApolloPropertyConfigs();

        for (ApolloPropertyConfig apolloPropertyConfig : apolloPropertyConfigs) {
            apolloPropertyConfig.addListener(configChangeListener);
        }
    }
}
