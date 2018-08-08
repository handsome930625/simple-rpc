package com.charse.register;

import java.util.List;

/**
 * description:
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 14:48
 */
public interface ServiceDiscovery {

    /**
     * description: 发现服务地址
     *
     * @param serviceName 服务名
     * @return 发现服务地址集合
     * @author 王亦杰
     * @date 2018/8/6 15:07
     */
    List<String> discover(String serviceName);
}
