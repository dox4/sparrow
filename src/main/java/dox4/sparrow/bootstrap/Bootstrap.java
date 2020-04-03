package dox4.sparrow.bootstrap;

import dox4.sparrow.connector.http.HttpConnector;

/**
 * @date 2020/4/2
 * @description 启动模块 - 启动类
 */
public class Bootstrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
