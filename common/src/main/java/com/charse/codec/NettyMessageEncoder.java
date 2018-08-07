package com.charse.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

/**
 * description: 编码器代理类
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/6 17:18
 */
public class NettyMessageEncoder extends MessageToByteEncoder {
    /**
     * 代理对象
     */
    private NettyMarshallingEncoder nettyMarshallingEncoder;
    /**
     * 适用类
     */
    private Class<?> genericClass;

    public NettyMessageEncoder(Class<?> genericClass) throws IOException {
        nettyMarshallingEncoder = MarshallingCodeCFactory.buildMarshallingEncoder();
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            ByteBuf sendBuf = Unpooled.buffer();
            // 长度预先占4个字节
            sendBuf.writeInt(0);
            nettyMarshallingEncoder.encode(ctx, msg, sendBuf);
            // 设置长度
            sendBuf.setInt(0, sendBuf.readableBytes());
            out.writeBytes(sendBuf);
        }
    }

}