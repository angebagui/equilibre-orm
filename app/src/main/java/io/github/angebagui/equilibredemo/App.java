package io.github.angebagui.equilibredemo;

import android.app.Application;

import io.github.angebagui.equilibre.Equilibre;
import io.github.angebagui.equilibre.EquilibreObject;
import io.github.angebagui.equilibredemo.model.User;

/**
 * Created by angebagui on 11/06/2016.
 */
public class App extends Application {
    private static final Integer DB_VERSION = 1;
    private static final String DB_NAME = "demo.db";

    @Override
    public void onCreate() {
        super.onCreate();

        Equilibre.initialize(getApplicationContext(),DB_NAME, DB_VERSION);
        EquilibreObject.registerSubclass(User.class);


    }
}
