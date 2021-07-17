package org.yx.sumk.apollo.boot;

import org.yx.bean.watcher.BootWatcher;
import org.yx.sumk.apollo.config.PropertyConfigProcessor;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author : wjiajun
 * @description: apollo 初始化处理
 */
public class BeanAfterWatcher implements BootWatcher {

    @Override
    public List<Class<?>> publish(List<Class<?>> list, Predicate<String> predicate) throws Exception {
        PropertyConfigProcessor propertyConfigProcessor = new PropertyConfigProcessor();
        propertyConfigProcessor.initializeAutoUpdate();
        return null;
    }

    @Override
    public int order() {
        return 1001;
    }
}
