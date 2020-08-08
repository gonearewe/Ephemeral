package com.mactavish.ephemeral.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

class Client {
    private static final String host = "127.0.0.1";
    private static final int port = 8080;

    void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpContentDecompressor());
                    ch.pipeline().addLast(new ClientHandler());
                }
            });
            ChannelFuture f = bootstrap.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}

class ClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private int step = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        System.out.println("got reply: " + msg.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        switch (step) {
            case 0:
                ctx.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));
                break;
            case 1:
                ctx.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/index"));
                break;
            case 2:
                ctx.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/person/jack/name"));
                break;
            case 3:
                ctx.writeAndFlush(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/library/story/number"));
                break;
        }

        step=(++step)%4;
    }
}
