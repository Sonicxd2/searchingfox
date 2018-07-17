package ru.searchingfox.network.packets;

public class Packet0Exception extends Packet {
    final Throwable throwable;

    public Packet0Exception(Throwable throwable) {
        super(0);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}


