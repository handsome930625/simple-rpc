package com.charse.client;

import com.charse.bean.RpcRequest;
import com.charse.bean.RpcResponse;
import com.charse.client.handler.ClientHandler;
import com.charse.codec.NettyMessageDecoder;
import com.charse.codec.NettyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 默认 rpc 客户端
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/7 10:00
 */
public class DefaultRpcClient {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRpcClient.class);
    /**
     * 已连接主机的缓存
     */
    private static Map<String, DefaultRpcClient> clientMap = new HashMap<String, DefaultRpcClient>();
    /**
     * host
     */
    private String host;
    /**
     * 端口号
     */
    private int port;

    private Channel channel;

    public DefaultRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static DefaultRpcClient getConnect(String host, int port) throws InterruptedException {
        if (clientMap.containsKey(host + port)) {
            return clientMap.get(host + port);
        }
        DefaultRpcClient con = connect(host, port);
        clientMap.put(host + port, con);
        return con;
    }

    private static DefaultRpcClient connect(String host, int port) throws InterruptedException {
        DefaultRpcClient client = new DefaultRpcClient(host, port);
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new NettyMessageEncoder(RpcRequest.class))
                        .addLast(new NettyMessageDecoder(RpcResponse.class))
                        .addLast(new ClientHandler());
            }
        });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        LOGGER.info("client connect to " + host + ":" + port);
        Channel c = future.channel();
        client.setChannel(c);
        return client;
    }

    public RpcResponse invoke(RpcRequest request) throws Exception {
        ClientHandler handle = channel.pipeline().get(ClientHandler.class);
        Assert.notNull(handle);
        return handle.invoke(request);
    }

    public Channel getChannel() {
        return channel;
    }

    private void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
