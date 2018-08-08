package com.charse.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;


/**
 * description: 解码器
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 17:18
 */
class NettyMarshallingDecoder extends MarshallingDecoder {

    NettyMarshallingDecoder(UnmarshallerProvider provider, int objectMaxSize) {
        super(provider, objectMaxSize);
    }

    public NettyMarshallingDecoder(UnmarshallerProvider provider) {
        super(provider);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        try {
            return super.decode(ctx, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
