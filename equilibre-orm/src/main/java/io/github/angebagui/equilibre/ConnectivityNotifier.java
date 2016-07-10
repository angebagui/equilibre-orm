
package io.github.angebagui.equilibre;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectivityNotifier extends BroadcastReceiver {
  private static final String TAG = ConnectivityNotifier.class.getSimpleName();
  public interface ConnectivityListener {
    void networkConnectivityStatusChanged(Context context, Intent intent);
  }

  private static final ConnectivityNotifier singleton = new ConnectivityNotifier();
  public static ConnectivityNotifier getNotifier(Context context) {
    singleton.tryToRegisterForNetworkStatusNotifications(context);
    return singleton;
  }

  public static boolean isConnected(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager == null) {
      return false;
    }

    NetworkInfo network = connectivityManager.getActiveNetworkInfo();
    return network != null && network.isConnected();
  }

  private Set<ConnectivityListener> listeners = new HashSet<>();
  private boolean hasRegisteredReceiver = false;
  private final Object lock = new Object();
  
  public void addListener(ConnectivityListener delegate) {
    synchronized (lock) {
      listeners.add(delegate);
    }
  }
  
  public void removeListener(ConnectivityListener delegate) {
    synchronized (lock) {
      listeners.remove(delegate);
    }
  }
  
  @SuppressLint("LongLogTag")
  private boolean tryToRegisterForNetworkStatusNotifications(Context context) {
    synchronized (lock) {
      if (hasRegisteredReceiver) {
        return true;
      }
      
      try {
        if (context == null) {
          return false;
        }
        context = context.getApplicationContext();
        context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        hasRegisteredReceiver = true;
        return true;
      } catch (ReceiverCallNotAllowedException e) {
        // In practice, this only happens with the push service, which will trigger a retry soon afterwards.
        Log.v(TAG, "Cannot register a broadcast receiver because the executing " +
                "thread is currently in a broadcast receiver. Will try again later.");
        return false;
      }
    }
  }
  
  @Override
  public void onReceive(Context context, Intent intent) {
    List<ConnectivityListener> listenersCopy;
    synchronized (lock) {
      listenersCopy = new ArrayList<>(listeners);
    }
    for (ConnectivityListener delegate : listenersCopy) {
      delegate.networkConnectivityStatusChanged(context, intent);
    }
  }
}
