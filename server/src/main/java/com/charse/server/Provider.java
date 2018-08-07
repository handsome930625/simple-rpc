package com.charse.server;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 服务提供者注解
 * 具有 @Service注解的能力
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/6 17:18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface Provider {
    /**
     * 服务发布名称
     * 如果为空字符串,默认会取全类名作为服务的发布名称
     */
    String value() default "";

    /**
     * 服务版本号
     */
    String version() default "";
}
