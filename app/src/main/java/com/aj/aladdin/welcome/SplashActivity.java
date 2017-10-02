package com.aj.aladdin.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.aj.aladdin.R;
import com.aj.aladdin.app.A;
import com.aj.aladdin.main.MainActivity;
import com.aj.aladdin.tools.oths.db.IO;

/**
 * Created by joan on 01/10/2017.
 */

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        IO.r.connect(); //// TODO: 01/10/2017 Manage reconnection or network unavailability

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (((A)getApplication()).getUser_id() != null)
                    MainActivity.start(SplashActivity.this);
                else
                    LoginActivity.start(SplashActivity.this);
            }
        }, SPLASH_TIME_OUT);
    }

}