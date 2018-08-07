package com.charse.client.handler;

import com.charse.bean.RpcRequest;
import com.charse.bean.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 客户端处理类
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/7 11:26
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    //request Id 与 response的映射
    private Map<Long, ResponseHolder> responseMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        ResponseHolder holder = responseMap.get(response.getRequestId());
        if (holder != null) {
            responseMap.remove(response.getRequestId());
            holder.setResponse(response);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exceptionCaught", cause);
        ctx.close();
    }

    public RpcResponse invoke(RpcRequest request) throws Exception {
        ResponseHolder holder = new ResponseHolder();
        responseMap.put(request.getRequestId(), holder);
        channel.writeAndFlush(request);
        return holder.getResponse();
    }

}
