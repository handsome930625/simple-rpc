package com.charse.register;

/**
 * description: 订阅节点 watch
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 17:07
 */
public interface SubscribeNode {
    /**
     * description: 如果出现订阅的节点发生了变化，可以执行runnable任务
     *
     * @param runnable    任务
     * @param serviceName 服务名
     * @author 王亦杰
     * @date 2018/8/6 17:10
     */
    void subscribe(String serviceName, Runnable runnable);
}
