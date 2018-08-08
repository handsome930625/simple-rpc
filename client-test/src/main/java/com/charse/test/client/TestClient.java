package com.charse.test.client;

import com.charse.test.api.HelloWorld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * description:
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 14:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class TestClient {
    @Autowired
    private HelloWorld helloWorld;

    @Test
    public void testSayHello() {
        helloWorld.sayHello();
    }

    @Test
    public void testSay(){
        System.out.println(helloWorld.say("我是陈惠杰的爸爸"));
    }
}
