package ru.searchingfox.network.example;

import ru.searchingfox.network.NettyClient;

public class TestClient {

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient();
        client.putRealization(0, (packet, ctx) -> {
            System.out.println(((Packet0Message)packet).getMessage());
        });
        boolean connected = false;
        connected = client.connect("localhost", 10420, true, 500, 400, (log) -> {
            System.out.println(log);
        });
        if(connected) {
            Thread.sleep(1000);
            client.sendPacket(new Packet0Message("Hello Server!"));
            client.sendPacket(new Packet1StopServer());
        }
    }
}
