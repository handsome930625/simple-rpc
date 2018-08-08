package com.charse.register;

/**
 * description:
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 15:05
 */
public interface ServiceRegistry {

    /**
     * description: 发布服务
     *
     * @param serviceName    服务名
     * @param serviceAddress 服务请求地址
     * @author 王亦杰
     * @date 2018/8/6 15:07
     */
    void register(String serviceName, String serviceAddress);
}
