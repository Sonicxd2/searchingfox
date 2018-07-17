package ru.searchingfox.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.searchingfox.network.packets.Packet;
import ru.searchingfox.network.utils.Pair;


import java.net.ConnectException;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NettyClient {

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private ClientHandler clientHandler;
    private HashMap<Integer, BiConsumer<Packet, ChannelHandlerContext>> realizations;

    public NettyClient() {
        init();
    }

    public void init() {
        realizations = new HashMap<>();
        group = new NioEventLoopGroup(4);

        clientHandler = new ClientHandler(realizations, null);

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast("object_encoder", new ObjectEncoder());
                        channelPipeline.addLast("object_decoder",
                                new ObjectDecoder(ClassResolvers.weakCachingResolver(getClass().getClassLoader())));
                        channelPipeline.addLast("client_handler", clientHandler);
                    }
                });
    }


    /**
     * Locking thread.
     * @param ip IP
     * @param port Port
     * @return Boolean - isConnected. String - reason why not connected.
     */
    public Pair<Boolean, String> connect(String ip, int port) {
        try {
            channelFuture = bootstrap.connect(ip, port).sync();
            if(channelFuture.isSuccess()) {
                return new Pair<>(true, "");
            }
            if(channelFuture.cause() != null) {
                return new Pair<>(false, channelFuture.cause().getMessage());
            }
            return new Pair<>(false, "No reason to cause!");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Pair<>(false, e.getMessage());
        } catch(Exception e) {
            return new Pair<>(false, e.getMessage());
        }
    }

    public boolean connect(String ip, int port, boolean autoReconnect, int cooldown) {
        if(autoReconnect == false) return connect(ip, port).getA();
        while(connect(ip, port).getA() != true) {
            try {
                Thread.sleep(cooldown);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param ip
     * @param port
     * @param autoReconnect
     * @param cooldown
     * @param attempts if < 1 do while don't connected
     * @param logger
     * @return is connected
     */
    public boolean connect(String ip, int port, boolean autoReconnect, int cooldown, int attempts, Consumer<String> logger) {
        if(attempts < 1) return connect(ip, port, autoReconnect, cooldown);

        int tryed = 0;
        while(connect(ip, port).getA() != true) {
            tryed++;
            logger.accept(String.format("%s/%s attempts to connect", tryed, attempts));
            if(tryed == attempts) break;

            try {
                Thread.sleep(cooldown);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return tryed != attempts;
    }

    public boolean connect(String ip, int port, boolean autoReconnect, int cooldown, int attempts) {
        return connect(ip, port, autoReconnect, cooldown, attempts, (string) -> {});
    }



    boolean isShutDowned = false;
    public void shutdown() {
        if(!isShutDowned) {
            isShutDowned = true;
            group.shutdownGracefully();
        }
    }

    public void sendPacket(Packet packet) {
        channelFuture.channel().write(packet);
        channelFuture.channel().flush();
    }

    public void putRealization(int id, BiConsumer<Packet, ChannelHandlerContext> handler) {
        realizations.put(id, handler);
    }

    public void setHelloPacket(Packet packet) {
        clientHandler.setHelloPacket(packet);
    }
}
