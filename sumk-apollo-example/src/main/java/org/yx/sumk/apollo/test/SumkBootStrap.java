package org.yx.sumk.apollo.test;

import org.yx.bean.IOC;
import org.yx.log.Log;
import org.yx.log.LogType;
import org.yx.main.StartConstants;
import org.yx.main.SumkServer;
import org.yx.sumk.apollo.test.service.AppInfoTest;
import org.yx.sumk.apollo.test.service.JsonValueTest;
import org.yx.sumk.apollo.test.service.ValueTest;

/**
 * @author : wjiajun
 */
public class SumkBootStrap {

    public static void main(String[] args) throws InterruptedException {
        Log.setLogType(LogType.slf4j);
        long begin = System.currentTimeMillis();
        SumkServer.start(StartConstants.NOHTTP, StartConstants.NOSOA, StartConstants.NOSOA_ClIENT);
        System.out.println("启动完成,除zookeeper服务器外耗时：" + (System.currentTimeMillis() - begin) + "毫秒");

        AppInfoTest appInfoTest = IOC.get(AppInfoTest.class);
        JsonValueTest jsonValueTest = IOC.get(JsonValueTest.class);
        ValueTest valueTest = IOC.get(ValueTest.class);

        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("appInfo............");
            appInfoTest.showTestValue();
            System.out.println("jsonValue............");
            jsonValueTest.showTestValue();
            System.out.println("value............");
            valueTest.showTestValue();
            System.out.println("method value............");
            valueTest.showTestMethodValue();
            System.out.println("method json value............");
            jsonValueTest.showTestMethodValue();
            Thread.sleep(5000);
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Log.printStack("main", e);
        }
    }
}
