package com.aj.aladdin.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.aj.aladdin.R;
import com.aj.aladdin.db.IO;
import com.aj.aladdin.welcome.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by joan on 01/10/2017.
 */

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        IO.r.connect(); // TODO: 01/10/2017 Manage reconnection or network unavailability and check if already connected
        //  |
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser() != null)
                    MainActivity.start(SplashActivity.this);
                else
                    LoginActivity.start(SplashActivity.this);
            }
        }, SPLASH_TIME_OUT);
    }

}