package ru.searchingfox.server.datasource.abstraction;

import com.google.common.cache.*;

import java.time.Duration;

public abstract class CacheObjectDataSource<K, V, D extends DataBaseSource> extends ObjectDataSource<K, V, D> {

    private LoadingCache<K, V> cache;
    public CacheObjectDataSource(D databaseSource, Duration duration) {
        super(databaseSource);
        cache =  CacheBuilder
                    .newBuilder()
                    .expireAfterAccess(duration)
                    .removalListener((RemovalListener<K, V>) notification -> {
                        try {
                            saveObject(notification.getKey(), notification.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K key) throws Exception {
                            return loadObject(key);
                        }
                    });
    }

    protected abstract V loadObject(K key) throws Exception;
    protected abstract void saveObject(K key, V value) throws Exception;

    @Override
    public void close() throws Exception {
        super.close();
    }

    public void invalidateCache() {
        cache.asMap().forEach((k, v) -> {
            try {
                saveObject(k, v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cache.invalidateAll();
    }

    @Override
    public V getObject(K key) throws Exception {
        return cache.get(key);
    }

    @Override
    public void setObject(K key, V value) throws Exception {
        cache.put(key, value);
        saveObject(key, value);
    }
}
