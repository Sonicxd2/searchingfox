package ru.searchingfox.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.searchingfox.network.packets.Packet;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class NettyServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture future;

    private HashMap<Integer, BiConsumer<Packet, ChannelHandlerContext>> realizations;

    public NettyServer() {
        init();
    }

    public void init() {
        realizations = new HashMap<>();

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(4);

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("object_encoder", new ObjectEncoder());
                        pipeline.addLast("object_decoder",
                                new ObjectDecoder(ClassResolvers.weakCachingResolver(getClass().getClassLoader())));
                        pipeline.addLast("server_handler", new ServerHandler(realizations));
                    }
                });
    }

    public void bind(int port) {
        try {
            future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //:(
        }
        shutdown();
    }

    private boolean isShutdowned = false;
    public void shutdown() {
        if(!isShutdowned) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            isShutdowned = true;
        }
    }


    public void putRealization(int id, BiConsumer<Packet, ChannelHandlerContext> handler) {
        realizations.put(id, handler);
    }
}
