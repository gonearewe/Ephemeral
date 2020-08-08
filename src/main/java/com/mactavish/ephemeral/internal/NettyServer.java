package com.mactavish.ephemeral.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;

public class NettyServer {
    private final int port;
    private final int numIOThreads, numWorkerThreads;

    public void start(ServerHandler serverHandler) throws Exception {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(this.numIOThreads);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(this.numWorkerThreads);
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new HttpRequestDecoder());
                    p.addLast(new HttpResponseEncoder());
                    p.addLast(new HttpObjectAggregator(65536));
                    p.addLast(new HttpContentCompressor());
                    // p.addLast(new ChunkedWriteHandler());
                    p.addLast(serverHandler);
                }
            });
            final ChannelFuture f = bootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    public NettyServer(int port, int numIOThreads, int numWorkerThreads) {
        this.port = port;
        this.numIOThreads = numIOThreads;
        this.numWorkerThreads = numWorkerThreads;
    }

    public NettyServer(int numIOThreads, int numWorkerThreads) {
        this(8080,numIOThreads,numWorkerThreads);
    }

    public NettyServer() {
        this(8080,2,5);
    }
}
