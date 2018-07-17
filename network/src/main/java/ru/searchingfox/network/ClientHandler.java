package ru.searchingfox.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.searchingfox.network.packets.Packet;

import java.util.HashMap;
import java.util.function.BiConsumer;

@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
    HashMap<Integer, BiConsumer<Packet, ChannelHandlerContext>> realizations;
    Packet helloPacket;

    public ClientHandler(HashMap<Integer, BiConsumer<Packet, ChannelHandlerContext>> realizations, Packet helloPacket) {
        this.realizations = realizations;
        this.helloPacket = helloPacket;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(helloPacket != null) {
            ctx.write(helloPacket);
            ctx.flush();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Packet) {
            realizations.get(((Packet) msg).getId()).accept((Packet) msg, ctx);
        } else {
            System.out.println("No packet!");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void setHelloPacket(Packet helloPacket) {
        this.helloPacket = helloPacket;
    }
}
