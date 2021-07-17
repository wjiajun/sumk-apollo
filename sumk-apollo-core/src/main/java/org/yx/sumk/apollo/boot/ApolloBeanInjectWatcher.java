package org.yx.sumk.apollo.boot;

import org.yx.annotation.Bean;
import org.yx.bean.watcher.BeanInjectWatcher;
import org.yx.sumk.apollo.property.ApolloAnnotationProcessor;
import org.yx.sumk.apollo.property.ApolloConfigProcessor;
import org.yx.sumk.apollo.property.SumkValueProcessor;

import java.util.List;

/**
 * @author : wjiajun
 * @description:
 */
@Bean
public class ApolloBeanInjectWatcher implements BeanInjectWatcher {

    @Override
    public void afterInject(List<Object> list) {
        ApolloConfigProcessor apolloConfigProcessor = new ApolloAnnotationProcessor();
        ApolloConfigProcessor sumkValueProcessor = new SumkValueProcessor();
        for (Object obj : list) {
            apolloConfigProcessor.processBean(obj);
            sumkValueProcessor.processBean(obj);
        }
    }
}
