package io.github.angebagui.equilibre;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by angebagui on 24/08/2015.
 */
public class Equilibre {

    public static final String TAG = Equilibre.class.getSimpleName();

    public static void initialize(Context context, String databaseName, Integer databaseVersion) {
        EquilibrePlugins.Android.initialize(context, databaseName, databaseVersion);
    }

    /**
     * @return {@code True} if {@link #initialize} has been called, otherwise {@code false}.
     */
    public static boolean isInitialized() {
        return EquilibrePlugins.get() != null;
    }

    public static Context getApplicationContext() {
        checkContext();
        return EquilibrePlugins.Android.get().applicationContext();
    }
    public static void checkInit() {
        if (EquilibrePlugins.get() == null) {
            throw new RuntimeException("You must call Equilibre.initialize(Context)"
                    + " before using the Equilibre library.");
        }

        if (EquilibrePlugins.get().getDatabaseName() == null) {
            throw new RuntimeException("databaseName is null. "
                    + "You must call Equilibre.initialize(Context)"
                    + " before using the Equilibre library.");
        }
        if (EquilibrePlugins.get().getDatabaseVersion() == null) {
            throw new RuntimeException("databaseVersion is null. "
                    + "You must call Equilibre.initialize(Context)"
                    + " before using the Equilibre library.");
        }
    }

    public static void checkContext() {
        if (EquilibrePlugins.Android.get().applicationContext() == null) {
            throw new RuntimeException("applicationContext is null. "
                    + "You must call Equilibre.initialize(Context)"
                    + " before using the Equilibre library.");
        }
    }

    public static boolean hasPermission(String permission) {
        return (getApplicationContext().checkCallingOrSelfPermission(permission) ==
                PackageManager.PERMISSION_GRANTED);
    }

    static void requirePermission(String permission) {
        if (!hasPermission(permission)) {
            throw new IllegalStateException(
                    "To use this functionality, add this to your AndroidManifest.xml:\n"
                            + "<uses-permission android:name=\"" + permission + "\" />");
        }
    }




}
