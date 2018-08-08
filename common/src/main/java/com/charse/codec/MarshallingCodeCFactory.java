package com.charse.codec;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.io.IOException;

/**
 * description: 使用 jboss marshall 编码
 * 对应的工厂类
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 17:18
 */
class MarshallingCodeCFactory {
    /**
     * marshall 工厂
     */
    private static final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");

    /**
     * description: marshalling 解码
     *
     * @author 王亦杰
     * @date 2018/8/7 9:25
     */
    static NettyMarshallingDecoder buildMarshallingDecoder() throws IOException {
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        return new NettyMarshallingDecoder(provider, 1024 << 2);
    }

    /**
     * description: marshalling 编码
     *
     * @author 王亦杰
     * @date 2018/8/7 9:25
     */
    static NettyMarshallingEncoder buildMarshallingEncoder() {
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        return new NettyMarshallingEncoder(provider);
    }
}
