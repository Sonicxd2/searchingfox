package ru.searchingfox.network.example;

import ru.searchingfox.network.NettyServer;

public class TestServer {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.putRealization(0, (packet, ctx) -> {
            System.out.println(((Packet0Message)packet).getMessage());
        });
        nettyServer.putRealization(1, (packet, ctx) -> {
            System.out.println("Stopping server!");
            try {
                ctx.writeAndFlush(new Packet0Message("Server stopped")).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nettyServer.shutdown();
        });

        nettyServer.bind(10420);
    }
}
