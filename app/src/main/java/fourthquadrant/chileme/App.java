package fourthquadrant.chileme;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;

import fourthquadrant.chileme.bean.CrashHandler;
import fourthquadrant.chileme.bean.Engine;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Euphoria on 2017/3/18.
 */

public class App extends Application {
    private static App sInstance;
    private Engine mEngine;
    public static RequestQueue queue;
    @Override
    public void onCreate() {
        super.onCreate();
        //CrashHandler.getInstance().init(this);
        //Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
        sInstance = this;
        Fresco.initialize(this);

        mEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/banner/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Engine.class);
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public static App getInstance() {
        return sInstance;
    }

    public Engine getEngine() {
        return mEngine;
    }

    public static RequestQueue getHttpQueue() { return queue; }

}