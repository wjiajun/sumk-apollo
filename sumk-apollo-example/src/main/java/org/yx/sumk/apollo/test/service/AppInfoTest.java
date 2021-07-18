package org.yx.sumk.apollo.test.service;

import org.yx.annotation.Bean;
import org.yx.conf.AppInfo;

/**
 * @author : wjiajun
 */
@Bean
public class AppInfoTest {

    public void showTestValue() {
        System.out.println(AppInfo.get("test.appInfoValue"));
    }
}
