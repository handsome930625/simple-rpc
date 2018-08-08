package com.charse.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;


/**
 * description: 解码器代理类
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 17:18
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {
    /**
     * 代理对象
     */
    private NettyMarshallingDecoder marshallingDecoder;
    /**
     * 解码器适用类
     */
    private Class<?> genericClass;

    public NettyMessageDecoder(Class<?> genericClass) throws IOException {
        marshallingDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 因为头4个字节是存放长度的 如果小于4个字节的包忽略
        if (in.readableBytes() < 4) {
            return;
        }
        // 判断长度是否足够
        in.markReaderIndex();
        int dataLength = in.readInt() - 4;
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // 解码
        Object value = marshallingDecoder.decode(ctx, in);
        if (genericClass.isInstance(value)) {
            out.add(value);
        }
    }
}