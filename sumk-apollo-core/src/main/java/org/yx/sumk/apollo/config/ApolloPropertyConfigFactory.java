package org.yx.sumk.apollo.config;

import com.ctrip.framework.apollo.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : wjiajun
 * @description:
 */
public class ApolloPropertyConfigFactory {

    private static final Logger logger = LoggerFactory.getLogger(ApolloPropertyConfigFactory.class);

    private final static List<ApolloPropertyConfig> CONFIGS = new LinkedList<>();

    public static ApolloPropertyConfig registry(String namespace, Config config) {
        logger.debug("set namespace:{}", namespace);
        ApolloPropertyConfig propertyConfig = new ApolloPropertyConfig(config);
        CONFIGS.add(propertyConfig);
        return propertyConfig;
    }

    public static List<ApolloPropertyConfig> getApolloPropertyConfigs() {
        return Collections.unmodifiableList(CONFIGS);
    }
}
