package com.charse.client.proxy;

import com.charse.register.RegistryCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Map;

/**
 * description: 服务代理bean 注入
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 13:05
 */
public class ServiceRegistryBean implements BeanDefinitionRegistryPostProcessor {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistryBean.class);
    /**
     * 引用服务类
     */
    private Map<String, String> refService;
    /**
     * 注册中心
     */
    private RegistryCenter registryCenter;

    public ServiceRegistryBean(Map<String, String> refService, RegistryCenter registryCenter) {
        this.refService = refService;
        this.registryCenter = registryCenter;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (Map.Entry<String, String> entry : refService.entrySet()) {
            String serviceName = entry.getKey();
            String className = entry.getValue();
            try {
                // 需要被代理的接口
                Class<?> cls = Class.forName(className);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
                definition.getPropertyValues().add("serviceName", serviceName);
                definition.getPropertyValues().add("registryCenter", registryCenter);
                definition.setBeanClass(RefProxyFactory.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                // 注册bean名,一般为类名首字母小写
                String springId = className.substring(className.lastIndexOf(".") + 1, className.length());
                beanDefinitionRegistry.registerBeanDefinition(lowerFirst(springId), definition);
            } catch (ClassNotFoundException e) {
                LOGGER.error("can not find the class :{},please check it !!", className);
                e.printStackTrace();
            }
        }
    }

    private String lowerFirst(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
