package fourthquadrant.chileme.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import fourthquadrant.chileme.R;

/**
 * Created by Euphoria on 2017/3/26.
 */

public class SplashScreenActivity extends Activity {
    protected int _splashTime = 2000;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Handler mDelay=new Handler();
        mDelay.postDelayed(new mSplashScreenHandler(),_splashTime);
    }
    class mSplashScreenHandler implements Runnable
    {
        public void run()
        {
            startActivity(new Intent(getApplication(),MainActivity.class));
            SplashScreenActivity.this.finish();
        }
    }
}
