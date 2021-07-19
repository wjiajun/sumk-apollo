package org.yx.sumk.apollo.boot;

import org.yx.bean.watcher.BootWatcher;
import org.yx.conf.AppInfo;
import org.yx.conf.ComposedConfig;
import org.yx.conf.SystemConfigHolder;
import org.yx.main.SumkServer;
import org.yx.sumk.apollo.config.ApolloSystemConfig;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author : wjiajun
 * apollo 初始化处理
 */
public class ApolloWatcher implements BootWatcher {

    protected volatile static ApolloSystemConfig apolloSystemConfig;

    @Override
    public List<Class<?>> publish(List<Class<?>> list, Predicate<String> predicate) throws Exception {
        apolloSystemConfig = new ApolloSystemConfig();
        Map<String, String> map = AppInfo.subMap("");
        //合并所有配置
        ComposedConfig composedConfig = new ComposedConfig(map, apolloSystemConfig);

        SystemConfigHolder.setSystemConfig(composedConfig);
        // 部分前置配置重新读取
        SumkServer.reloadConfig();
        return null;
    }

    protected static void notifyUpdate() {
        apolloSystemConfig.onRefresh();
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }
}
