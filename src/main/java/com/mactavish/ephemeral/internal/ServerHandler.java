package com.mactavish.ephemeral.internal;

import com.mactavish.ephemeral.Request;
import com.mactavish.ephemeral.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Routers routers;
    public ServerHandler(Routers routers){
        this.routers=routers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        Response response=this.routers.route(new Request(msg.method(),msg.uri(),msg.headers()));
        response.writeTo(ctx);
    }
}
