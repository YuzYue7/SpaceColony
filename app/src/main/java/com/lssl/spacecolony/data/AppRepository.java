package com.lssl.spacecolony.data;

public class AppRepository {
    private static AppRepository instance;

    private final Storage storage;

    private AppRepository() {
        storage = new Storage();
    }

    public static AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }
        return instance;
    }

    public Storage getStorage() {
        return storage;
    }
}