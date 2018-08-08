package com.charse.test.server;

import com.charse.server.Provider;
import com.charse.test.api.HelloWorld;

/**
 * description:
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 10:15
 */
// value 是暴露到外部的服务名
@Provider(value = "HelloWorld")
public class HelloWorldImpl implements HelloWorld {
    @Override
    public void sayHello() {
        System.out.println("hello world");
    }

    @Override
    public String say(String world) {
        return "server receive " + world;
    }
}
