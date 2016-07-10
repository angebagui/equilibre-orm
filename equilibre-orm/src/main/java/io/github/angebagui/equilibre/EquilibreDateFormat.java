package io.github.angebagui.equilibre;

/**
 * Created by angebagui on 20/08/2015.
 */



import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * This is the currently used date format. It is precise to the millisecond.
 */
public class EquilibreDateFormat {
    private static final String TAG = EquilibreDateFormat.class.getSimpleName();
    private static final EquilibreDateFormat INSTANCE = new EquilibreDateFormat();
    public static EquilibreDateFormat getInstance(){
        return INSTANCE;
    }

    // SimpleDateFormat isn't inherently thread-safe
    private final Object lock = new Object();

    private final DateFormat dateFormat;

    private EquilibreDateFormat(){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(new SimpleTimeZone(0, "GMT"));
        dateFormat = format;
    }

    public Date parse(String dateString){
        synchronized (lock){
            try {
                return dateFormat.parse(dateString);
            }catch (ParseException e){
                Log.e(TAG, "could not parse date:" + dateString, e);
                return null;
            }
        }
    }

    public String format(Date date){
        synchronized (lock){
            return dateFormat.format(date);
        }
    }
}
