package ru.searchingfox.server.datasource.abstraction;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public abstract class ConfigDataSource<T> extends DataSource {
    protected File configurationFile;
    protected T object;

    public ConfigDataSource(File file) {
        this.configurationFile = file;
    }

    protected abstract void loadConfiguration() throws Exception;
    protected abstract void saveConfiguration() throws Exception;

    public Optional<T> getObject() {
        return Optional.of(object);
    }

    @Override
    public void start() throws Exception {
        loadConfiguration();
        Objects.requireNonNull(object, "Not yet loaded");
    }

    @Override
    public void close() throws Exception {
        saveConfiguration();
    }
}
