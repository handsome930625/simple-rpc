package com.charse.server;

import com.charse.bean.RpcRequest;
import com.charse.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.Map;

/**
 * description:
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/7 9:58
 */
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    /**
     * handler map
     */
    private final Map<String, Object> handlerMap;

    public ServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (!validate(request)) {
            return;
        }
        // 创建并初始化 RPC 响应对象
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setSuccess(true);
            response.setResult(result);
        } catch (Exception e) {
            LOGGER.error("handle result failure", e);
            response.setException(e);
            response.setSuccess(false);
        }
        // 写入 RPC 响应对象并自动关闭连接
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }

    /**
     * description: 通过反射调用目标方法，并且返回返回值
     *
     * @param request rpc请求类
     * @return rpcResponse
     * @author 王亦杰（yijie.wang01@ucarinc.com）
     * @date 2018/8/7 11:13
     */
    private Object handle(RpcRequest request) throws Exception {
        String serviceName = request.getInterfaceName();
        String version = request.getServiceVersion();
        if (StringUtils.isNotEmpty(version)) {
            serviceName += "-" + version;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
        }
        // 获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    /**
     * description:
     *
     * @param request rpc 请求类
     * @return 是否允许访问
     * @author 王亦杰（yijie.wang01@ucarinc.com）
     * @date 2018/8/7 11:09
     */
    private boolean validate(RpcRequest request) {
        return request != null
                && request.getRequestId() != null
                && !StringUtils.isBlank(request.getInterfaceName())
                && !StringUtils.isBlank(request.getMethodName());
    }


}
