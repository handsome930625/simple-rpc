package com.charse.client.proxy;

import com.charse.register.RegistryCenter;
import org.springframework.beans.factory.FactoryBean;

/**
 * description: 服务动态代理工厂
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 13:30
 */
public class RefProxyFactory<T> implements FactoryBean<T> {
    /**
     * 代理接口
     */
    private Class<T> interfaceClass;
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 注册中心
     */
    private RegistryCenter registryCenter;

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        return (T) new RefProxy().bind(interfaceClass, serviceName, registryCenter);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        // 单例模式
        return true;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setRegistryCenter(RegistryCenter registryCenter) {
        this.registryCenter = registryCenter;
    }
}
