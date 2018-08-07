package com.charse.client.proxy;

import com.charse.bean.RpcRequest;
import com.charse.bean.RpcResponse;
import com.charse.client.DefaultRpcClient;
import com.charse.exception.NotFindServiceException;
import com.charse.register.RegistryCenter;
import com.charse.utils.IdWorker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * description:
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/7 13:32
 */
public class RefProxy implements InvocationHandler {
    /**
     * 全局唯一算法
     */
    private IdWorker idWorker = new IdWorker(1);
    /**
     * netty 客户端
     */
    private DefaultRpcClient client = null;
    /**
     * 本地服务路径
     */
    private List<String> localUrlList = null;

    private RegistryCenter registryCenter;

    private String serviceName;

    private static final Object lock = new Object();

    public <T> Object bind(Class<T> interfaceClass, String serviceName, RegistryCenter registryCenter) {
        this.registryCenter = registryCenter;
        this.serviceName = serviceName;
        // 1. 发现服务 保存到本地
        localUrlList = registryCenter.discover(serviceName);
        // 2. 注册监听
        registryCenter.subscribe(serviceName, new SubscriptionRunnable());
        // 3. 生成代理
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return this.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return this.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return this.equals(args[0]);
        }
        RpcRequest request = new RpcRequest();
        request.setRequestId(idWorker.nextId());
        request.setInterfaceName(serviceName);
        request.setMethodName(methodName);
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        String address;
        if (CollectionUtils.isEmpty(localUrlList)) {
            synchronized (lock) {
                address = getUrl(3);
            }
            if (StringUtils.isBlank(address)) {
                throw new NotFindServiceException("No effective service was found");
            } else {
                registryCenter.subscribe(serviceName, new SubscriptionRunnable());
            }
        } else {
            address = localUrlList.get(ThreadLocalRandom.current().nextInt(localUrlList.size()));
        }
        if (client == null) {
            String[] addressArray = address.split(":");
            client = DefaultRpcClient.getConnect(addressArray[0], Integer.valueOf(addressArray[1]));
        }
        RpcResponse r = client.invoke(request);
        return r.getResult();
    }

    private String getUrl(int times) {
        if (CollectionUtils.isEmpty(localUrlList) && times >= 0) {
            localUrlList = registryCenter.discover(serviceName);
            times--;
            getUrl(times);
        } else if (CollectionUtils.isNotEmpty(localUrlList)) {
            return localUrlList.get(ThreadLocalRandom.current().nextInt(localUrlList.size()));
        }
        return null;
    }

    class SubscriptionRunnable implements Runnable {
        @Override
        public void run() {
            // 1. 重新获取注册表
            localUrlList = registryCenter.discover(serviceName);
            // 2. 注册监听
            registryCenter.subscribe(serviceName, new SubscriptionRunnable());
        }
    }
}
