package com.mactavish.ephemeral.internal;

import com.mactavish.ephemeral.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Routers routers;
    ServerHandler(Routers routers){
        this.routers=routers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        new Request(msg.uri(),msg.method());
    }

}
