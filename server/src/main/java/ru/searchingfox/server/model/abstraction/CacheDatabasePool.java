package ru.searchingfox.server.model.abstraction;

import ru.searchingfox.server.datasource.abstraction.CacheObjectDataSource;
import ru.searchingfox.server.datasource.abstraction.DataBaseSource;

import java.util.List;

public interface CacheDatabasePool {
    void addCache(CacheObjectDataSource cache);
    void removeCache(CacheObjectDataSource cache);

    void addDatabase(DataBaseSource dataBaseSource);
    void removeDatabase(DataBaseSource dataBaseSource);

    List<CacheObjectDataSource> asCacheList();

    void invalidateCaches();
    void invalidateDatabases();

    void invalidateOnExit();

}
