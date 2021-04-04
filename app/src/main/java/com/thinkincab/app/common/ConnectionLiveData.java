package com.thinkincab.app.common;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class ConnectionLiveData extends LiveData<Boolean> {

    private final ConnectivityManager connectivityManager;
    private final Context context;
    private ConnectivityManager.NetworkCallback connectivityManagerCallback;

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnection();
        }
    };

    public ConnectionLiveData(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        super.onActive();
        updateConnection();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(getConnectivityManagerCallback());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lollipopNetworkAvailableRequest();
        } else {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            context.registerReceiver(networkReceiver, filter);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback);
        } else {
            context.unregisterReceiver(networkReceiver);
        }
    }

    private void updateConnection() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        postValue(activeNetwork != null && activeNetwork.isConnected());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lollipopNetworkAvailableRequest() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder()
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI);
        connectivityManager.registerNetworkCallback(builder.build(), getConnectivityManagerCallback());
    }

    private ConnectivityManager.NetworkCallback getConnectivityManagerCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManagerCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    postValue(true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    postValue(false);
                }
            };
            return connectivityManagerCallback;
        } else {
            throw new IllegalAccessError("Should not happened");
        }
    }
}
