package com.charse.test.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * description:
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 10:16
 */
public class test {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(test.class);

    public static void main(String[] args) {
        LOGGER.debug("start server");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
