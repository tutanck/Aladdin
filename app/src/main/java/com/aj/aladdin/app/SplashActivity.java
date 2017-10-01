/**
 * Created by joan on 01/10/2017.
 */

package com.aj.aladdin.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.aj.aladdin.R;
import com.aj.aladdin.main.MainActivity;
import com.aj.aladdin.tools.oths.db.IO;
import com.aj.aladdin.welcome.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        IO.r.connect(); //// TODO: 01/10/2017 Manage reconnection or network unavailability

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null)
                    MainActivity.start(SplashActivity.this, currentUser.getUid());
                else
                    LoginActivity.start(SplashActivity.this);
            }
        }, SPLASH_TIME_OUT);
    }

}