package org.yx.sumk.apollo.test.service;

import org.yx.annotation.Bean;
import org.yx.sumk.apollo.annotation.Value;

/**
 * @author : wjiajun
 */
@Bean
public class ValueTest {

    @Value("${test.value}")
    private String testValue;

    private String testMethodValue;

    public void showTestValue() {
        System.out.println(testValue);
    }

    @Value("${test.method.value}")
    public void setTestMethodValue(String value) {
        testMethodValue = value;
    }

    public void showTestMethodValue() {
        System.out.println(testMethodValue);
    }
}
