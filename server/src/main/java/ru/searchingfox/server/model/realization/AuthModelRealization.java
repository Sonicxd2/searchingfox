package ru.searchingfox.server.model.realization;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.searchingfox.server.datasource.realization.SQLiteDataSource;
import ru.searchingfox.server.model.abstraction.*;
import ru.searchingfox.server.model.realization.cache.AuthCacheRealization;

import java.io.UnsupportedEncodingException;
import java.time.Duration;

public class AuthModelRealization implements AuthModel {
    private AuthCacheRealization cache;

    public AuthModelRealization(SQLiteDataSource dataSource) {
        cache = new AuthCacheRealization(dataSource, Duration.ofSeconds(10));
        try {
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public AuthModelRealization(SQLiteDataSource dataSource, CacheDatabasePool cacheDatabasePool) {
        this(dataSource);
        cacheDatabasePool.addCache(cache);
    }

    private AuthResult auth(AuthPlayer authPlayer, String nickname, String hash) {
        return authPlayer.authorisation(nickname, hash) ? AuthResult.SUCCESS : AuthResult.ERROR;
    }

    private AuthResult auth(String nickname, String hash) {
        String loadedHash;
        try {
            loadedHash = cache.getObject(nickname);
        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
        return auth(new AuthPlayerRealization(nickname, loadedHash), nickname, hash);
    }

    private RegisterResult reg(String nickname, String hash) {
        try {
            cache.setObject(nickname, hash);
        } catch (Exception e) {
            e.printStackTrace();
            return RegisterResult.ERROR;
        }
        return RegisterResult.SUCCESS;
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public AuthResult authorization(String nickname, String password) {
        String hash = new String(Hashing.sha512().hashBytes(password.getBytes("UTF-8")).asBytes(), "UTF-8");
        return auth(nickname, hash);
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public RegisterResult register(String nickname, String password) {
        String hash = new String(Hashing.sha512().hashBytes(password.getBytes("UTF-8")).asBytes(), "UTF-8");
        return reg(nickname, hash);
    }

    private enum Querying {
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS \"Auth\"(\"Nickname\" TEXT NOT NULL, \"Password\" text, PRIMARY KEY (\"Nickname\"));"),
        GET_OBJECT("SELECT * FROM Auth WHERE Nickname=\"?\";"),
        SAVE_OBJECT("INSERT INTO Auth VALUES(\"?\", \"?\");");

        @Getter String query;

        Querying(String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return query;
        }
    }
}
