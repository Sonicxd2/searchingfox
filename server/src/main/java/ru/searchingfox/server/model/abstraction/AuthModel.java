package ru.searchingfox.server.model.abstraction;

public interface AuthModel {
    AuthResult authorization(String nickname, String password);
    RegisterResult register(String nickname, String password);
}
