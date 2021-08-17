package org.yx.sumk.apollo.boot;

import org.yx.annotation.Bean;
import org.yx.bean.watcher.BeanInjectWatcher;
import org.yx.sumk.apollo.property.apollo.AnnotationProcessor;
import org.yx.sumk.apollo.property.ConfigProcessor;
import org.yx.sumk.apollo.property.value.SumkValueProcessor;

import java.util.List;

/**
 * @author : wjiajun
 */
@Bean
public class ApolloBeanInjectWatcher implements BeanInjectWatcher {

    @Override
    public void afterInject(List<Object> list) {
        ConfigProcessor configProcessor = new AnnotationProcessor();
        ConfigProcessor sumkValueProcessor = new SumkValueProcessor();
        for (Object obj : list) {
            configProcessor.processBean(obj);
            sumkValueProcessor.processBean(obj);
        }
    }
}
