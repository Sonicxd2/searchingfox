package ru.searchingfox.server.model.realization;

import ru.searchingfox.server.model.abstraction.AuthPlayer;

public class AuthPlayerRealization implements AuthPlayer {
    String nickname;
    String hash;

    public AuthPlayerRealization(String nickname, String hash) {
        this.nickname = nickname;
        this.hash = hash;
    }

    @Override
    public boolean authorisation(String nickname, String hash) {
        return nickname.equals(this.nickname) && hash.equals(this.hash);
    }
}
