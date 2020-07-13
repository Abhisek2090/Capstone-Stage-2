package com.example.newsapplication.application;

import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.example.newsapplication.utils.ConnectivityReceiver;

import io.fabric.sdk.android.Fabric;

public class NewsApplication extends MultiDexApplication {

    private static NewsApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .build();

        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
    }


    public static synchronized NewsApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }



}
