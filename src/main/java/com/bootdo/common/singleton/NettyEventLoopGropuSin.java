package com.bootdo.common.singleton;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class NettyEventLoopGropuSin {



    private static class SingletonClassInstance{
        private static final EventLoopGroup eventLoopGroupInstance=new NioEventLoopGroup();
    }

    private  NettyEventLoopGropuSin(){}

    public static EventLoopGroup getInstance() {
        return SingletonClassInstance.eventLoopGroupInstance;
    }


}
