package ru.searchingfox.server.datasource.abstraction;

public abstract class ObjectDataSource<K, V, D extends DataBaseSource> extends DataSource {
    protected D databaseSource;

    public ObjectDataSource(D databaseSource) {
        if(!checkDatabaseSource(databaseSource)) {
            throw new IllegalArgumentException();
        }
        this.databaseSource = databaseSource;
    }

    protected abstract boolean checkDatabaseSource(D databaseSource);

    public abstract V getObject(K key) throws Exception;
    public abstract void setObject(K key, V value) throws Exception;
    protected abstract void postInitializeDataSource() throws Exception;

    @Override
    public void start() throws Exception {
        postInitializeDataSource();
    }

    @Override
    public void close() throws Exception {
    }
}
