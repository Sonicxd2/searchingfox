package ru.searchingfox.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.searchingfox.server.datasource.realization.SQLiteDataSource;
import ru.searchingfox.server.model.abstraction.AuthModel;
import ru.searchingfox.server.model.abstraction.CacheDatabasePool;
import ru.searchingfox.server.model.abstraction.GameModel;
import ru.searchingfox.server.model.realization.AuthModelRealization;
import ru.searchingfox.server.model.realization.CacheDatabasePoolRealization;
import ru.searchingfox.server.model.realization.GameModelRealization;
import ru.searchingfox.server.netty.Server;
import ru.searchingfox.server.utils.Console;

public class Bootstrap {
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        SQLiteDataSource database = new SQLiteDataSource("database.sqlite3");
        CacheDatabasePool cacheDatabasePool = new CacheDatabasePoolRealization();
        cacheDatabasePool.invalidateOnExit();
        database.start();
        cacheDatabasePool.addDatabase(database);

        AuthModel authModel = new AuthModelRealization(database, cacheDatabasePool);
        GameModel gameModel = new GameModelRealization(database, cacheDatabasePool);

        Server server = new Server(authModel, gameModel);
        server.initServer(1337);

        Console console = new Console();

        console.addCommand((arg) -> {
            System.exit(0);
        }, "stop");

        console.run();
        System.out.println("0");
    }
}
