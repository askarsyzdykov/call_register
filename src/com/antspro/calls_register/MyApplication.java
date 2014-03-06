package com.antspro.calls_register;

import android.app.Application;
import android.os.Bundle;
import com.ubertesters.sdk.Ubertesters;

/**
 * Created by arna on 05.03.14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Ubertesters.initialize(this);
    }
}
