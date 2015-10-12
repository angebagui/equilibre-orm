package com.equilibre;

import android.content.Context;

/**
 * Created by angebagui on 24/08/2015.
 */
public class EquilibrePlugins {

    private String databaseName;
    private Integer databaseVersion;

    private static EquilibrePlugins instance;
    private static final Object LOCK = new Object();

    private EquilibrePlugins(){

    }

    private EquilibrePlugins(String databaseName, int databaseVersion){
        this.databaseName = databaseName;
        this.databaseVersion = databaseVersion;
    }
    static void initialize(String databaseName, int databaseVersion){
        EquilibrePlugins.set(new EquilibrePlugins(databaseName, databaseVersion));
    }
    public static EquilibrePlugins get(){
        synchronized (LOCK) {
            return instance;
        }
    }

    public Integer getDatabaseVersion() {
        return databaseVersion;
    }

    public String getDatabaseName() {
        return databaseName;
    }


    public static void set(EquilibrePlugins plugins){
        synchronized (LOCK){
            if (instance!=null){
                throw new IllegalStateException("EquilibrePlugins is already initialized");
            }
            instance = plugins;
        }
    }

    static class Android extends EquilibrePlugins{
        private Context applicationContext;

        private Android(Context context,String databaseName, Integer databaseVersion) {
            super(databaseName, databaseVersion);
            applicationContext = context.getApplicationContext();
        }
        static void initialize(Context context, String databaseName, int databaseVersion){
            EquilibrePlugins.set(new Android(context, databaseName, databaseVersion));
        }
        public static Android get(){
            return (Android) EquilibrePlugins.get();
        }
        public Context applicationContext(){
            return applicationContext;
        }
    }
}
