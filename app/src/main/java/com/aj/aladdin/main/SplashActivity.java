package com.aj.aladdin.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.aj.aladdin.R;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.welcome.LoginActivity;

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
                if (((A)getApplication()).getUser_id() != null) //// TODO: 03/10/2017 check what todo if auth current user is not null
                    MainActivity.start(SplashActivity.this);
                else
                    LoginActivity.start(SplashActivity.this);
            }
        }, SPLASH_TIME_OUT);
    }

}