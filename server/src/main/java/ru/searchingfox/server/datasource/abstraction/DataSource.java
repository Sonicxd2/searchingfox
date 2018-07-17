package ru.searchingfox.server.datasource.abstraction;

public abstract class DataSource {
    public abstract void start() throws Exception;

    public abstract void close() throws Exception;
}
