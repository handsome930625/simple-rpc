package com.charse.server;

import com.charse.bean.RpcRequest;
import com.charse.bean.RpcResponse;
import com.charse.codec.NettyMessageDecoder;
import com.charse.codec.NettyMessageEncoder;
import com.charse.register.RegistryCenter;
import com.charse.utils.AddressUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 默认的服务启动类
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/7 8:45
 */
public class DefaultRpcServer implements ApplicationContextAware, InitializingBean {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRpcServer.class);
    /**
     * 注册中心 目前只有zookeeper
     */
    private RegistryCenter registryCenter;
    /**
     * 发布远程服务端口号
     */
    private int port;
    /**
     * handlerServiceMap 是否初始化了
     */
    private static boolean isHandlerServiceMapInit = false;
    /**
     * key = 服务名 value = 服务类对象
     */
    private static Map<String, Object> handlerServiceMap = new HashMap<>();
    /**
     * 初始化锁 static 防止2个实例
     */
    private static final Object lock = new Object();

    public DefaultRpcServer(RegistryCenter registryCenter, int port) {
        this.registryCenter = registryCenter;
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动netty
        synchronized (lock) {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                // 创建并初始化 Netty 服务端 Bootstrap 对象
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup);
                bootstrap.channel(NioServerSocketChannel.class);
                bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new NettyMessageDecoder(RpcRequest.class)); // 解码 RPC 请求
                        pipeline.addLast(new NettyMessageEncoder(RpcResponse.class)); // 编码 RPC 响应
                        pipeline.addLast(new ServerHandler(handlerServiceMap)); // 处理 RPC 请求
                    }
                });
                bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                String ip = AddressUtils.getLocalHost();
                // 启动 RPC 服务器
                ChannelFuture future = bootstrap.bind(ip, port).sync();
                String address = ip + ":" + port;
                // 注册 RPC 服务地址
                for (String serviceName : handlerServiceMap.keySet()) {
                    registryCenter.register(serviceName, address);
                    LOGGER.debug("register service: {} => {}", serviceName, address);
                }
                LOGGER.debug("server started on port {}", port);
                // 关闭 RPC 服务器 线程阻塞在这 lock 对象永远被持有
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LOGGER.debug("server stop on port {}", port);
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 初始化 handle map
        if (!isHandlerServiceMapInit) {
            synchronized (lock) {
                Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(Provider.class);
                if (MapUtils.isNotEmpty(serviceBeanMap)) {
                    for (Object serviceBean : serviceBeanMap.values()) {
                        Provider provider = serviceBean.getClass().getAnnotation(Provider.class);
                        String serviceName = provider.value();
                        if (StringUtils.isBlank(serviceName)) {
                            serviceName = serviceBean.getClass().getName();
                        }
                        String serviceVersion = provider.version();
                        if (StringUtils.isNotBlank(serviceVersion)) {
                            serviceName += "-" + serviceVersion;
                        }
                        handlerServiceMap.put(serviceName, serviceBean);
                    }
                }
                isHandlerServiceMapInit = true;
            }
        }
    }
}
