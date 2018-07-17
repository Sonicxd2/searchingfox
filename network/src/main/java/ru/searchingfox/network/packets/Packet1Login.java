package ru.searchingfox.network.packets;

public class Packet1Login extends Packet {
    String username;
    String password;

    public Packet1Login(String username, String password) {
        super(1);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
