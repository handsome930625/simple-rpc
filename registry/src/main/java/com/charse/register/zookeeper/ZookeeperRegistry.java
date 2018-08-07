package com.charse.register.zookeeper;

import com.charse.register.RegistryCenter;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 基于zookeeper的注册中心
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/6 15:09
 */
public class ZookeeperRegistry implements RegistryCenter {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistry.class);
    /**
     * 会话超时时间
     */
    private static final int SESSION_TIME_OUT = 5000;
    /**
     * 连接超时时间
     */
    private static final int CONNECTION_TIMEOUT = 1000;
    /**
     * zk客户端
     */
    private volatile static ZkClient instance = null;
    /**
     * 注册中心根目录
     */
    private static final String REGISTRY_PATH = "/simple-rpc-register";
    /**
     * zk地址
     */
    private String zkAddress;

    public ZookeeperRegistry(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    /**
     * description: 获取zk客户端
     *
     * @return zookeeper 客户端
     * @author 王亦杰（yijie.wang01@ucarinc.com）
     * @date 2018/8/6 16:40
     */
    private ZkClient getZkClient() {
        if (instance == null) {
            synchronized (ZkClient.class) {
                if (instance == null)
                    instance = new ZkClient(zkAddress, SESSION_TIME_OUT, CONNECTION_TIMEOUT);
            }
        }
        return instance;
    }

    @Override
    public List<String> discover(String serviceName) {
        LOGGER.debug("connect zookeeper");
        // 获取 service 节点
        String servicePath = REGISTRY_PATH + "/" + serviceName;
        ZkClient zkClient = getZkClient();
        List<String> addressDataList = new ArrayList<>();
        if (!zkClient.exists(servicePath)) {
            LOGGER.warn("can not find any service node on path: {}", servicePath);
            return addressDataList;
        }
        List<String> addressNodeList = zkClient.getChildren(servicePath);
        if (CollectionUtils.isEmpty(addressNodeList)) {
            return addressDataList;
        }
        for (String addressNode : addressNodeList) {
            // 获取 address 节点的值
            String addressPath = servicePath + "/" + addressNode;
            String addressData = zkClient.readData(addressPath);
            addressDataList.add(addressData);
        }
        return addressDataList;
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（临时）
        ZkClient zkClient = getZkClient();
        if (!zkClient.exists(REGISTRY_PATH)) {
            zkClient.createPersistent(REGISTRY_PATH);
            LOGGER.debug("create registry node: {}", REGISTRY_PATH);
        }
        // 创建 service 节点（临时）
        String servicePath = REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create address node: {}", addressNode);
    }


    @Override
    public void subscribe(final String serviceName, final Runnable runnable) {
        getZkClient().subscribeChildChanges(REGISTRY_PATH + "/" + serviceName, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                LOGGER.debug("node change :{}", serviceName);
                // 钩子方法
                runnable.run();
            }
        });
    }
}
