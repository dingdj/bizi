package com.mklodoss.SexyGirl;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by dingdj on 2014/8/4.
 */
public class MainApplication extends Application{

    RequestQueue queue;

    public static  MainApplication _application;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        _application = this;
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public void setQueue(RequestQueue queue) {
        this.queue = queue;
    }
}
