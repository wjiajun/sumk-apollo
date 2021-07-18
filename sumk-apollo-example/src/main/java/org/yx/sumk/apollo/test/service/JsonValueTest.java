package org.yx.sumk.apollo.test.service;

import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import org.yx.annotation.Bean;

/**
 * @author : wjiajun
 */
@Bean
public class JsonValueTest {

    @ApolloJsonValue("${test.jsonValue}")
    private JsonObject jsonObject;

    private JsonObject jsonObjectMethod;

    public void showTestValue() {
        System.out.println(jsonObject);
    }

    @ApolloJsonValue("${test.jsonMethodValue}")
    public void setTestValue(JsonObject param) {
        jsonObjectMethod = param;
    }

    public void showTestMethodValue() {
        System.out.println(jsonObjectMethod);
    }

    static class JsonObject {
        private String value;

        @Override
        public String toString() {
            return "JsonObject{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }
}
