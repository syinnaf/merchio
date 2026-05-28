package com.example.merchio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {

    public interface NetworkListener {
        void onNetworkChange(boolean isConnected);
    }

    private final NetworkListener listener;

    public NetworkReceiver(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm =
                (ConnectivityManager)
                        context.getSystemService(
                                Context.CONNECTIVITY_SERVICE
                        );

        NetworkInfo activeNetwork =
                cm.getActiveNetworkInfo();

        boolean isConnected =
                activeNetwork != null
                        && activeNetwork.isConnected();

        if(listener != null) {
            listener.onNetworkChange(isConnected);
        }
    }
}