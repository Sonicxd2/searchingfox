package ru.searchingfox.server.model.abstraction;

public interface AuthPlayer {
    AuthPlayer EMPTY_REALIZATION = (nickname, hash) -> false;

    /**
     * Hash in SHA512.
     * @param nickname
     * @param hash
     * @return
     */
    boolean authorisation(String nickname, String hash);
}
