package ru.searchingfox.server.model.realization;

import ru.searchingfox.server.datasource.abstraction.CacheObjectDataSource;
import ru.searchingfox.server.datasource.abstraction.DataBaseSource;
import ru.searchingfox.server.model.abstraction.CacheDatabasePool;

import java.util.ArrayList;
import java.util.List;

public class CacheDatabasePoolRealization implements CacheDatabasePool {
    ArrayList<CacheObjectDataSource> caches = new ArrayList<>();
    ArrayList<DataBaseSource> databases = new ArrayList<>();

    @Override
    public void addCache(CacheObjectDataSource cache) {
        caches.add(cache);
    }

    @Override
    public void removeCache(CacheObjectDataSource cache) {
        caches.remove(cache);
    }

    @Override
    public void addDatabase(DataBaseSource dataBaseSource) {
        databases.add(dataBaseSource);
    }

    @Override
    public void removeDatabase(DataBaseSource dataBaseSource) {
        databases.add(dataBaseSource);
    }

    @Override
    public List<CacheObjectDataSource> asCacheList() {
        return caches;
    }

    @Override
    public void invalidateCaches() {
        caches.forEach((cache) -> cache.invalidateCache());
    }

    @Override
    public void invalidateDatabases() {
        databases.forEach((database) -> database.close());
    }

    @Override
    public void invalidateOnExit() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                invalidateCaches();
                invalidateDatabases();
            }
        });
    }
}
