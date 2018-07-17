package ru.searchingfox.server.datasource.realization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Cleanup;
import ru.searchingfox.server.datasource.abstraction.ConfigDataSource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;

public class JsonDataSource<T> extends ConfigDataSource<T> {
    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private Type configType;

    public JsonDataSource(File file) {
        super(file);
        this.configType = new TypeToken<T>(){}.getType();
    }

    public JsonDataSource(File file, T defaultValue) {
        super(file);
        this.object = defaultValue;
        this.configType = new TypeToken<T>(){}.getType();
    }


    @Override
    protected void loadConfiguration() throws Exception {
        @Cleanup FileReader fileReader = new FileReader(configurationFile);
        T configObject = prettyGson.fromJson(fileReader, configType);
        if(configObject != null)
            this.object = configObject;
    }

    @Override
    protected void saveConfiguration() throws Exception {
        @Cleanup FileWriter fileWriter = new FileWriter(configurationFile);
        prettyGson.toJson(object, configType, fileWriter);
    }
}
