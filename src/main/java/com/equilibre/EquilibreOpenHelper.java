package com.equilibre;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by angebagui on 24/08/2015.
 */
public class EquilibreOpenHelper extends OrmLiteSqliteOpenHelper {

    private static EquilibreOpenHelper instance;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    public EquilibreOpenHelper(Context context) {
        super(context, EquilibrePlugins.get().getDatabaseName(),null, EquilibrePlugins.get().getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        Equilibre.checkInit();
        Collection<Class<? extends EquilibreObject>> tableCollections = EquilibreObject.getClasses();

        Class<? extends EquilibreObject> tablesArray[] = new Class[tableCollections.size()];
        Class<? extends EquilibreObject> tables[] = tableCollections.toArray(tablesArray);
        Log.i(Equilibre.TAG, "DB Creation");
        try {

            for (int i=0; i<tables.length; i++){
                Log.i(Equilibre.TAG, "\t CREATE IF NOT EXISTS ====> " + tables[i].getName());
                TableUtils.createTableIfNotExists(connectionSource, tables[i]);
            }

        }catch (SQLException e){
            Log.e(Equilibre.TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Equilibre.checkInit();
        Collection<Class<? extends EquilibreObject>> tableCollections = EquilibreObject.getClasses();

        Class<? extends EquilibreObject> tablesArray[] = new Class[tableCollections.size()];
        Class<? extends EquilibreObject> tables[] = tableCollections.toArray(tablesArray);
        try {
            for (int i=0; i<tables.length; i++){
                Log.i(Equilibre.TAG, "\t DROP TABLE ==> "+tables[i].getName());
                TableUtils.dropTable(connectionSource, tables[i], true);
            }
            this.onCreate(database,connectionSource);
        }catch (SQLException e){
            Log.e(EquilibreOpenHelper.class.getSimpleName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() {
        super.close();
    }

    /**
     * Singleton Get the Helper
     *
     * @param context your  context
     * @return See {@link EquilibreOpenHelper}
     */
    public static synchronized EquilibreOpenHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (EquilibreOpenHelper.class) {
                if (instance == null)
                    instance = new EquilibreOpenHelper(context);
            }
        }
        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }
}
